package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_PLACE_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.StopPlaceCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.stopplace.StopPlaceApiWriter;
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
  private final StopPlaceApiWriter stopPlaceApiWriter;
  private final StopPlaceCsvService stopPlaceCsvService;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  @StepScope
  @Bean
  public ThreadSafeListItemReader<StopPlaceCsvModelContainer> stopPlaceListItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    final List<StopPlaceCsvModel> actualStopPlaceCsvModels;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualStopPlaceCsvModels = stopPlaceCsvService.getActualCsvModels(file);
    } else {
      actualStopPlaceCsvModels = stopPlaceCsvService.getActualCsvModelsFromS3();
    }
    final List<StopPlaceCsvModelContainer> stopPlaceCsvModelContainers =
        stopPlaceCsvService.mapToStopPlaceCsvModelContainers(actualStopPlaceCsvModels);
    long prunedStopPlaceModels = stopPlaceCsvModelContainers.stream()
        .collect(Collectors.summarizingInt(value -> value.getCreateStopPlaceVersionModels().size())).getSum();
    log.info("Found " + prunedStopPlaceModels + " stopPlaces to import...");
    log.info("Start sending requests to service-point-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(stopPlaceCsvModelContainers));
  }

  @Bean
  public Step parseStopPlaceCsvStep(ThreadSafeListItemReader<StopPlaceCsvModelContainer> stopPlaceListItemReader) {
    String stepName = "parseStopPlaceCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPlaceCsvModelContainer, StopPlaceCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
        .reader(stopPlaceListItemReader)
        .writer(stopPlaceApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(prmAsyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importStopPlaceCsvJob(ThreadSafeListItemReader<StopPlaceCsvModelContainer> stopPlaceListItemReader) {
    return new JobBuilder(IMPORT_STOP_PLACE_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseStopPlaceCsvStep(stopPlaceListItemReader))
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
