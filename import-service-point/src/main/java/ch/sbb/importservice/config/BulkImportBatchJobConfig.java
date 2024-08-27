package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_JOB_NAME;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.listener.BulkImportDataExecutionToLogFileListener;
import ch.sbb.importservice.listener.BulkImportDataValidationToLogFileListener;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.bulk.reader.BulkImportReaders;
import ch.sbb.importservice.service.bulk.writer.BulkImportWriters;
import ch.sbb.importservice.utils.StepUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BulkImportBatchJobConfig {

  private static final int CHUNK_SIZE = 20;
  private static final int THREAD_EXECUTION_SIZE = 64;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final BulkImportWriters bulkImportWriters;
  private final BulkImportReaders bulkImportReaders;
  private final BulkImportDataValidationToLogFileListener bulkImportDataValidationToLogFileListener;
  private final BulkImportDataExecutionToLogFileListener bulkImportDataExecutionToLogFileListener;

  @Bean
  public Job bulkImportJob(ThreadSafeListItemReader<BulkImportUpdateContainer<?>> itemReader, ItemWriter<BulkImportUpdateContainer<?>> itemWriter) {
    return new JobBuilder(BULK_IMPORT_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(bulkImportFromCsv(itemReader, itemWriter))
        .end()
        .build();
  }

  @Bean
  public Step bulkImportFromCsv(ThreadSafeListItemReader<BulkImportUpdateContainer<?>> itemReader,
      ItemWriter<BulkImportUpdateContainer<?>> itemWriter) {
    String stepName = "bulkImportFromCsv";
    return new StepBuilder(stepName, jobRepository)
        .<BulkImportUpdateContainer<?>, BulkImportUpdateContainer<?>>chunk(CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .listener(bulkImportDataValidationToLogFileListener)
        .writer(itemWriter)
        .listener(bulkImportDataExecutionToLogFileListener)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<BulkImportUpdateContainer<?>> itemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile,
      @Value("#{jobParameters[application]}") String application,
      @Value("#{jobParameters[objectType]}") String objectType,
      @Value("#{jobParameters[importType]}") String importType
  ) {

    BulkImportConfig config = BulkImportConfig.builder()
        .application(ApplicationType.valueOf(application))
        .objectType(BusinessObjectType.valueOf(objectType))
        .importType(ImportType.valueOf(importType))
        .build();
    Function<File, List<BulkImportUpdateContainer<?>>> readerFunction = bulkImportReaders.getReaderFunction(config);

    List<BulkImportUpdateContainer<?>> items = new ArrayList<>();
    if (pathToFile != null) {
      File file = new File(pathToFile);
      items.addAll(readerFunction.apply(file));
    }

    // Should we download the file here instead of passing it via job param?

    log.info("Bulk import configured with chunkSize: {}", CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(items);
  }

  @StepScope
  @Bean
  public ItemWriter<BulkImportUpdateContainer<?>> itemWriter(
      @Value("#{jobParameters[application]}") String application,
      @Value("#{jobParameters[objectType]}") String objectType,
      @Value("#{jobParameters[importType]}") String importType
  ) {

    BulkImportConfig config = BulkImportConfig.builder()
        .application(ApplicationType.valueOf(application))
        .objectType(BusinessObjectType.valueOf(objectType))
        .importType(ImportType.valueOf(importType))
        .build();
    return items -> bulkImportWriters.getWriter(config).accept(items);
  }

  @StepScope
  @Bean
  protected TaskExecutor asyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
