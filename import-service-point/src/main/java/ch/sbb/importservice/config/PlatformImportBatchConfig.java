package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PLATFORM_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.PlatformCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.PlatformApiWriter;
import java.io.File;
import java.util.Collections;
import java.util.List;
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
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class PlatformImportBatchConfig extends BaseImportBatchJob {

  private static final int PRM_CHUNK_SIZE = 20;
  private final PlatformApiWriter platformApiWriter;
  private final PlatformCsvService platformCsvService;

  protected PlatformImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
      PlatformApiWriter platformApiWriter, PlatformCsvService platformCsvService) {
    super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
    this.platformApiWriter = platformApiWriter;
    this.platformCsvService = platformCsvService;
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<PlatformCsvModelContainer> platformListItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    List<PlatformCsvModel> actualPlatformCsvModels;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualPlatformCsvModels = platformCsvService.getActualCsvModels(file);
    } else {
      actualPlatformCsvModels = platformCsvService.getActualCsvModelsFromS3();
    }
    List<PlatformCsvModelContainer> platformCsvModelContainers = platformCsvService.mapToPlatformCsvModelContainers(
        actualPlatformCsvModels);
    long prunedPlatformModels = platformCsvModelContainers.stream()
        .mapToLong(i -> i.getCreateModels().size()).sum();
    log.info("Found " + prunedPlatformModels + " platforms to import...");
    log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(platformCsvModelContainers));
  }

  @Bean
  public Step parsePlatformCsvStep(ThreadSafeListItemReader<PlatformCsvModelContainer> platformListItemReader) {
    String stepName = "parsePlatformCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<PlatformCsvModelContainer, PlatformCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
        .reader(platformListItemReader)
        .writer(platformApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importPlatformCsvJob(ThreadSafeListItemReader<PlatformCsvModelContainer> platformListItemReader) {
    return new JobBuilder(IMPORT_PLATFORM_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parsePlatformCsvStep(platformListItemReader))
        .end()
        .build();
  }

}
