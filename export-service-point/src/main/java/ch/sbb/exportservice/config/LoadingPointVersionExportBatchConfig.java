package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.entity.LoadingPointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import ch.sbb.exportservice.processor.LoadingPointVersionCsvProcessor;
import ch.sbb.exportservice.reader.LoadingPointVersionRowMapper;
import ch.sbb.exportservice.reader.LoadingPointVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvLoadingPointVersionWriter;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LoadingPointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvLoadingPointVersionWriter csvLoadingPointVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<LoadingPointVersion> loadingPointReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<LoadingPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(LoadingPointVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new LoadingPointVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportLoadingPointCsvStep(ItemReader<LoadingPointVersion> itemReader) {
    final String stepName = "exportLoadingPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<LoadingPointVersion, LoadingPointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(loadingPointVersionCsvProcessor())
        .writer(loadingPointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LoadingPointVersionCsvProcessor loadingPointVersionCsvProcessor() {
    return new LoadingPointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<LoadingPointVersionCsvModel> loadingPointCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvLoadingPointVersionWriter.csvWriter(exportType, ExportFileName.LOADING_POINT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME)
  public Job exportLoadingPointCsvJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLoadingPointCsvStep(itemReader))
        .next(uploadLoadingPointCsvFileStep())
        .next(deleteTrafficPointElementCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadLoadingPointCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadLoadingPointCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadLoadingPointCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return new UploadCsvFileTasklet(exportType, ExportFileName.LOADING_POINT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME)
  public Job exportLoadingPointJsonJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementJsonStep(itemReader))
        .next(uploadTrafficPointElementJsonFileStep())
        .next(deleteTrafficPointElementJsonFileStep())
        .end()
        .build();
  }

}
