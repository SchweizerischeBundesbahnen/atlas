package ch.sbb.importservice.module.geo.job;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.importservice.config.listener.StepTracerListener;
import ch.sbb.importservice.config.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.module.geo.listener.GeoLocationJobCompletionListener;
import ch.sbb.importservice.module.geo.service.ServicePointUpdateGeoLocationService;
import ch.sbb.importservice.module.geo.writer.ServicePointUpdateGeoLocationApiWriter;
import ch.sbb.importservice.utils.StepUtils;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.chunk.ChunkTaskExecutorItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ServicePointGeoLocationUpdateConfig {

  public static final String GEO_LOCATION_VERSIONS_KEY = "GeoLocationVersions";
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
  public ThreadSafeListItemReader<ServicePointSwissWithGeoLocationModel> geoLocationItemReader(
      @Value("#{stepExecution}") StepExecution stepExecution) {
    List<ServicePointSwissWithGeoLocationModel> servicePointWithGeolocation =
        geoLocationService.getActualServicePointWithGeolocation();
    stepExecution.getExecutionContext().put(GEO_LOCATION_VERSIONS_KEY, servicePointWithGeolocation.size());
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(servicePointWithGeolocation));
  }

  @StepScope
  @Bean
  public ChunkTaskExecutorItemWriter<ServicePointSwissWithGeoLocationModel> geoItemWriter() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setThreadNamePrefix("geo-update-");
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.afterPropertiesSet();
    return new ChunkTaskExecutorItemWriter<>(geoApiWriter, taskExecutor);
  }

  @Bean
  public Step updateServicePointGeoLocationStep(
      ThreadSafeListItemReader<ServicePointSwissWithGeoLocationModel> geoLocationItemReader,
      ChunkTaskExecutorItemWriter<ServicePointSwissWithGeoLocationModel> geoItemWriter) {
    String stepName = "updateServicePointGeoLocationStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointSwissWithGeoLocationModel, ServicePointSwissWithGeoLocationModel>chunk(SERVICE_POINT_CHUNK_SIZE)
        .transactionManager(transactionManager)
        .reader(geoLocationItemReader)
        .writer(geoItemWriter)
        .faultTolerant()
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Job updateServicePointGeoJob(Step updateServicePointGeoLocationStep) {
    return new JobBuilder(UPDATE_SERVICE_POINT_GEO_JOB, jobRepository)
        .listener(geoLocationJobCompletionListener)
        .flow(updateServicePointGeoLocationStep)
        .end()
        .build();
  }

}
