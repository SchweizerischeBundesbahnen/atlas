package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_DIDOK_USER_CSV_JOB_NAME;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.entity.user.UserCsvModel;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.DidokUserCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.user.UserApiWriter;
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
public class DidokUserImportBatchConfig extends BaseImportBatchJob {

  private static final int PRM_CHUNK_SIZE = 20;
  private final UserApiWriter userApiWriter;
  private final DidokUserCsvService didokUserCsvService;

  protected DidokUserImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
      UserApiWriter userApiWriter, DidokUserCsvService didokUserCsvService) {
    super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
    this.userApiWriter = userApiWriter;
    this.didokUserCsvService = didokUserCsvService;
  }

  @StepScope
  @Bean
  public ThreadSafeListItemReader<UserCsvModel> userCsvModelListItemReader(
      @Value("#{jobParameters[fullPathFileName]}") String pathToFile,
      @Value("#{jobParameters[applicationType]}") ApplicationType applicationType) {
    final List<UserCsvModel> userCsvModels;
    if (applicationType == null) {
      throw new IllegalStateException("ApplicationType must be provided");
    }
    if (pathToFile == null) {
      throw new IllegalStateException("File must be provided!");
    }
    File file = new File(pathToFile);
    userCsvModels = didokUserCsvService.getUserCsvModels(file, applicationType);
    log.info("Found " + userCsvModels + " user to import...");
    log.info("Start sending requests to user-service with chunkSize: {}...", PRM_CHUNK_SIZE);
    return new ThreadSafeListItemReader<>(Collections.synchronizedList(userCsvModels));
  }

  @Bean
  public Step parseUserCsvStep(ThreadSafeListItemReader<UserCsvModel> userListItemReader) {
    String stepName = "parseUserCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<UserCsvModel, UserCsvModel>chunk(PRM_CHUNK_SIZE, transactionManager)
        .reader(userListItemReader)
        .writer(userApiWriter)
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importDidokUserCsvJob(ThreadSafeListItemReader<UserCsvModel> userListItemReader) {
    return new JobBuilder(IMPORT_DIDOK_USER_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseUserCsvStep(userListItemReader))
        .end()
        .build();
  }

}
