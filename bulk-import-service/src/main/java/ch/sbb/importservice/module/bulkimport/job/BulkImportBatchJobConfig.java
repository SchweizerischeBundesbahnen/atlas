package ch.sbb.importservice.module.bulkimport.job;

import static ch.sbb.importservice.utils.JobDescriptionConstants.BULK_IMPORT_JOB_NAME;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.config.listener.StepTracerListener;
import ch.sbb.importservice.config.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.module.bulkimport.listener.BulkImportDataValidationToLogFileListener;
import ch.sbb.importservice.module.bulkimport.listener.BulkImportJobCompletionListener;
import ch.sbb.importservice.module.bulkimport.log.BulkImportLogService;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import ch.sbb.importservice.module.bulkimport.reader.BulkImportReaders;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportWriters;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import ch.sbb.importservice.utils.StepUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkTaskExecutorItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BulkImportBatchJobConfig {

  private static final int CHUNK_SIZE = 20;
  private static final int THREAD_EXECUTION_SIZE = 10;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final BulkImportJobCompletionListener bulkImportJobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final BulkImportWriters bulkImportWriters;
  private final BulkImportReaders bulkImportReaders;
  private final BulkImportDataValidationToLogFileListener bulkImportDataValidationToLogFileListener;
  private final BulkImportLogService bulkImportLogService;

  @Bean
  public Job bulkImportJob(Step bulkImportFromCsv) {
    return new JobBuilder(BULK_IMPORT_JOB_NAME, jobRepository)
        .listener(bulkImportJobCompletionListener)
        .flow(bulkImportFromCsv)
        .end()
        .build();
  }

  @Bean
  public Step bulkImportFromCsv(ThreadSafeListItemReader<BulkImportUpdateContainer<?>> itemReader,
      ChunkTaskExecutorItemWriter<BulkImportUpdateContainer<?>> asyncItemWriter) {
    String stepName = "bulkImportFromCsv";
    return new StepBuilder(stepName, jobRepository)
        .<BulkImportUpdateContainer<?>, BulkImportUpdateContainer<?>>chunk(CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(itemReader)
        .listener(bulkImportDataValidationToLogFileListener)
        .writer(asyncItemWriter)
        .faultTolerant()
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<BulkImportUpdateContainer<?>> itemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile,
      @Value("#{jobParameters[application]}") String application,
      @Value("#{jobParameters[objectType]}") String objectType,
      @Value("#{jobParameters[importType]}") String importType,
      @Value("#{jobParameters[bulkImportId]}") Long bulkImportId
  ) {

    BulkImportConfig config = BulkImportConfig.builder()
        .application(ApplicationType.valueOf(application))
        .objectType(BusinessObjectType.valueOf(objectType))
        .importType(ImportType.valueOf(importType))
        .build();
    Function<File, List<BulkImportUpdateContainer<?>>> readerFunction = bulkImportReaders.getReaderFunction(config);

    File file = new File(Objects.requireNonNull(pathToFile));
    List<BulkImportUpdateContainer<?>> items = new ArrayList<>(readerFunction.apply(file));
    items.forEach(item -> item.setBulkImportId(bulkImportId));
    return new ThreadSafeListItemReader<>(items);
  }

  @StepScope
  @Bean
  public ChunkTaskExecutorItemWriter<BulkImportUpdateContainer<?>> asyncItemWriter(
      @Value("#{jobParameters[application]}") String application,
      @Value("#{jobParameters[objectType]}") String objectType,
      @Value("#{jobParameters[importType]}") String importType,
      @Value("#{stepExecution}") StepExecution stepExecution) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setThreadNamePrefix("bulk-import-");
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.afterPropertiesSet();

    BulkImportConfig config = BulkImportConfig.builder()
        .application(ApplicationType.valueOf(application))
        .objectType(BusinessObjectType.valueOf(objectType))
        .importType(ImportType.valueOf(importType))
        .build();
    BulkImportItemWriter writer = bulkImportWriters.getWriter(config);
    ChunkProcessor<BulkImportUpdateContainer<?>> chunkProcessor = (items, contribution) -> {

      WriterUtil.addInNameOfTo(contribution.getStepExecution(), items.getItems());
      List<BulkImportUpdateContainer<?>> itemsToWrite = WriterUtil.getContainersWithoutDataValidationErrors(items);
      try {
        writer.accept(itemsToWrite);
        contribution.incrementWriteCount(items.getItems().size());
      } catch (Exception e) {
        itemsToWrite.forEach(item -> item.setBulkImportLogEntry(BulkImportLogEntry.builder()
            .status(BulkImportStatus.DATA_EXECUTION_ERROR)
            .lineNumber(item.getLineNumber())
            .errors(List.of(BulkImportError.builder()
                .errorMessage(e.getMessage())
                .build()))
            .build()));

        contribution.incrementWriteSkipCount(items.getItems().size());
        contribution.setExitStatus(ExitStatus.FAILED.setExitException(e));
      }
      items.getItems().forEach(writeItem ->
          bulkImportLogService.saveDataExecutionLog(stepExecution.getJobExecutionId(), writeItem));
    };
    return new ChunkTaskExecutorItemWriter<>(chunkProcessor, new DelegatingSecurityContextTaskExecutor(taskExecutor));
  }

}
