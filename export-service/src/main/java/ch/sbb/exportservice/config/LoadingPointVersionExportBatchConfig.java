package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.exportservice.entity.prm.LoadingPointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import ch.sbb.exportservice.processor.LoadingPointVersionCsvProcessor;
import ch.sbb.exportservice.processor.LoadingPointVersionJsonProcessor;
import ch.sbb.exportservice.reader.LoadingPointVersionRowMapper;
import ch.sbb.exportservice.reader.LoadingPointVersionSqlQueryUtil;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvLoadingPointVersionWriter;
import ch.sbb.exportservice.writer.JsonLoadingPointVersionWriter;
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
import org.springframework.batch.item.json.JsonFileItemWriter;
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
  private final JsonLoadingPointVersionWriter jsonLoadingPointVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<LoadingPointVersion> loadingPointReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<LoadingPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(LoadingPointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvLoadingPointVersionWriter.csvWriter(ExportObjectV2.LOADING_POINT, exportTypeV2);
  }

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME)
  public Job exportLoadingPointCsvJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLoadingPointCsvStep(itemReader))
        //        .next(uploadLoadingPointCsvFileStep())
        //        .next(deleteLoadingPointCsvFileStep())
        .end()
        .build();
  }

  /*@Bean
  public Step uploadLoadingPointCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadLoadingPointCsvFileTasklet(null), transactionManager)
        .tasklet(uploadLoadingPointCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadLoadingPointCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadLoadingPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.LOADING_POINT_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteLoadingPointCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(loadingPointCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet loadingPointCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }*/

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME)
  public Job exportLoadingPointJsonJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLoadingPointJsonStep(itemReader))
        //        .next(uploadLoadingPointJsonFileStep())
        //        .next(deleteLoadingPointJsonFileStep())
        .end()
        .build();
  }

  /*@Bean
  public Step uploadLoadingPointJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadLoadingPointJsonFileTasklet(null), transactionManager)
        .tasklet(uploadLoadingPointJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadLoadingPointJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadLoadingPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.LOADING_POINT_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteLoadingPointJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileLoadingPointJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileLoadingPointJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }*/

  @Bean
  public Step exportLoadingPointJsonStep(ItemReader<LoadingPointVersion> itemReader) {
    String stepName = "exportLoadingPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<LoadingPointVersion, ReadLoadingPointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(loadingPointVersionJsonProcessor())
        .writer(loadingPointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LoadingPointVersionJsonProcessor loadingPointVersionJsonProcessor() {
    return new LoadingPointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadLoadingPointVersionModel> loadingPointJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonLoadingPointVersionWriter.getWriter(ExportObjectV2.LOADING_POINT, exportTypeV2);
  }

}
