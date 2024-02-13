package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TOILET_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.ToiletCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.ToiletApiWriter;
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
public class ToiletImportBatchConfig extends BaseImportBatchJob {

  private static final int PRM_CHUNK_SIZE = 20;
  private final ToiletApiWriter toiletApiWriter;
  private final ToiletCsvService toiletCsvService;

  protected ToiletImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
      ToiletApiWriter toiletApiWriter, ToiletCsvService toiletCsvService) {
    super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
    this.toiletApiWriter = toiletApiWriter;
    this.toiletCsvService = toiletCsvService;
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<ToiletCsvModelContainer> toiletListItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
    List<ToiletCsvModel> actualToiletCsvModels;
    if (pathToFile != null) {
      File file = new File(pathToFile);
      actualToiletCsvModels = toiletCsvService.getActualCsvModels(file);
    } else {
      actualToiletCsvModels = toiletCsvService.getActualCsvModelsFromS3();
    }
    List<ToiletCsvModelContainer> toiletCsvModelContainers = toiletCsvService.mapToToiletCsvModelContainers(
        actualToiletCsvModels);
    long prunedToiletModels = toiletCsvModelContainers.stream()
        .mapToLong(i -> i.getCreateModels().size()).sum();
    log.info("Found " + prunedToiletModels + " toilets to import...");
    log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(toiletCsvModelContainers));
  }

  @Bean
  public Step parseToiletCsvStep(ThreadSafeListItemReader<ToiletCsvModelContainer> toiletListItemReader) {
    String stepName = "parseToiletCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ToiletCsvModelContainer, ToiletCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
        .reader(toiletListItemReader)
        .writer(toiletApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importToiletCsvJob(ThreadSafeListItemReader<ToiletCsvModelContainer> toiletListItemReader) {
    return new JobBuilder(IMPORT_TOILET_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseToiletCsvStep(toiletListItemReader))
        .end()
        .build();
  }

}
