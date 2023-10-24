package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_POINT_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.StopPointCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.stoppoint.StopPointApiWriter;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
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
public class PRMImportBatchConfig {

  private static final int PRM_CHUNK_SIZE = 20;

  private static final int THREAD_EXECUTION_SIZE = 64;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final StopPointApiWriter stopPointApiWriter;
  private final StopPointCsvService stopPointCsvService;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  @StepScope
  @Bean
  public ThreadSafeListItemReader<StopPointCsvModelContainer> stopPointListItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    final List<StopPointCsvModel> actualStopPointCsvModels;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualStopPointCsvModels = stopPointCsvService.getActualCsvModels(file);
    } else {
      actualStopPointCsvModels = stopPointCsvService.getActualCsvModelsFromS3();
    }
    final List<StopPointCsvModelContainer> stopPointCsvModelContainers =
        stopPointCsvService.mapToStopPointCsvModelContainers(actualStopPointCsvModels);
    long prunedStopPointModels = stopPointCsvModelContainers.stream()
        .collect(Collectors.summarizingInt(value -> value.getCreateStopPointVersionModels().size())).getSum();
    log.info("Found " + prunedStopPointModels + " stopPoints to import...");
    log.info("Start sending requests to service-point-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(stopPointCsvModelContainers));
  }

  @Bean
  public Step parseStopPointCsvStep(ThreadSafeListItemReader<StopPointCsvModelContainer> stopPointListItemReader) {
    String stepName = "parseStopPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPointCsvModelContainer, StopPointCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
        .reader(stopPointListItemReader)
        .writer(stopPointApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(prmAsyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importStopPointCsvJob(ThreadSafeListItemReader<StopPointCsvModelContainer> stopPointListItemReader) {
    return new JobBuilder(IMPORT_STOP_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseStopPointCsvStep(stopPointListItemReader))
        .end()
        .build();
  }

  @Bean
  public TaskExecutor prmAsyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
