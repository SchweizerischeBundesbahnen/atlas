package ch.sbb.importservice.job.geo;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.importservice.listener.GeoLocationJobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.geo.ServicePointUpdateGeoLocationApiWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ServicePointGeoLocationUpdateConfig {

  public static final String UPDATE_SERVICE_POINT_GEO_JOB = "updateServicePointGeoJob";
  private static final int SERVICE_POINT_CHUNK_SIZE = 40;
  private static final int THREAD_EXECUTION_SIZE = 64;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final GeoLocationJobCompletionListener geoLocationJobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final ServicePointUpdateGeoLocationService geoLocationService;
  private final ServicePointUpdateGeoLocationApiWriter geoApiWriter;

  @StepScope
  @Bean
  public ThreadSafeListItemReader<ServicePointSwissWithGeoLocationModel> servicePointGeoLocationListItemReader() {
    List<ServicePointSwissWithGeoLocationModel> servicePointWithGeolocation =
        geoLocationService.getActualServicePointWithGeolocation();
    log.info("Start sending requests to service-point-directory with chunkSize: {}...", SERVICE_POINT_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(servicePointWithGeolocation));
  }

  @Bean
  public Step updateServicePointGeoLocationStep(
      ThreadSafeListItemReader<ServicePointSwissWithGeoLocationModel> servicePointListItemReader) {
    String stepName = "parseServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointSwissWithGeoLocationModel, ServicePointSwissWithGeoLocationModel>chunk(SERVICE_POINT_CHUNK_SIZE,
            transactionManager)
        .reader(servicePointListItemReader)
        .writer(geoApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncGeoLocationTaskExecutor())
        .build();
  }

  @Bean
  public Job updateServicePointGeoJob(
      ThreadSafeListItemReader<ServicePointSwissWithGeoLocationModel> servicePointListItemReader) {
    return new JobBuilder(UPDATE_SERVICE_POINT_GEO_JOB, jobRepository)
        .listener(geoLocationJobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(updateServicePointGeoLocationStep(servicePointListItemReader))
        .end()
        .build();
  }

  @StepScope
  @Bean
  protected TaskExecutor asyncGeoLocationTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
