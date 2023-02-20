package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;

import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.CsvService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.LoadingPointApiWriter;
import ch.sbb.importservice.writer.ServicePointApiWriter;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@Slf4j
public class SpringBatchConfig {

  private static final int CHUNK_SIZE = 20;
  private static final int THREAD_EXECUTION_SIZE = 64;

  private final JobRepository jobRepository;

  private final PlatformTransactionManager transactionManager;

  private final ServicePointApiWriter servicePointApiWriter;
  private final LoadingPointApiWriter loadingPointApiWriter;
  private final CsvService csvService;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  private final JobHelperService jobHelperService;

  @StepScope
  @Bean
  public ThreadSafeListItemReader<ServicePointCsvModelContainer> servicePointlistItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    List<ServicePointCsvModelContainer> actualServicePotinCsvModelsFromS3;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualServicePotinCsvModelsFromS3 = csvService.getActualServicePointCsvModels(file);
    } else {
      actualServicePotinCsvModelsFromS3 = csvService.getActualServicePointCsvModelsFromS3();
    }
    log.info("Start sending requests to service-point-directory with chunkSize: {}...",
        jobHelperService.getServicePointDirectoryChunkSize());
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(actualServicePotinCsvModelsFromS3));
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<LoadingPointCsvModel> loadingPointlistItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    List<LoadingPointCsvModel> actualLoadingPotinCsvModelsFromS3;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualLoadingPotinCsvModelsFromS3 = csvService.getActualLoadingPointCsvModels(file);
    } else {
      actualLoadingPotinCsvModelsFromS3 = csvService.getActualLoadingPointCsvModelsFromS3();
    }

    return new ThreadSafeListItemReader<>(Collections.synchronizedList(actualLoadingPotinCsvModelsFromS3));
  }

  @Bean
  public Step parseServicePointCsvStep(ThreadSafeListItemReader<ServicePointCsvModelContainer> servicePointlistItemReader) {
    String stepName = "parseServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointCsvModelContainer, ServicePointCsvModelContainer>chunk(CHUNK_SIZE, transactionManager)
        .reader(servicePointlistItemReader)
        .writer(servicePointApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Step parseLoadingPointCsvStep(ThreadSafeListItemReader<LoadingPointCsvModel> loadingPointlistItemReader) {
    String stepName = "parseLoadingPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<LoadingPointCsvModel, LoadingPointCsvModel>chunk(CHUNK_SIZE, transactionManager)
        .reader(loadingPointlistItemReader)
        .writer(loadingPointApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importServicePointCsvJob(ThreadSafeListItemReader<ServicePointCsvModelContainer> servicePointlistItemReader) {
    return new JobBuilder(IMPORT_SERVICE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseServicePointCsvStep(servicePointlistItemReader))
        .end()
        .build();
  }

  @Bean
  public Job importLoadingPointCsvJob(ThreadSafeListItemReader<LoadingPointCsvModel> loadingPointlistItemReader) {
    return new JobBuilder(IMPORT_LOADING_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseLoadingPointCsvStep(loadingPointlistItemReader))
        .end()
        .build();
  }

  @Bean
  public TaskExecutor asyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
