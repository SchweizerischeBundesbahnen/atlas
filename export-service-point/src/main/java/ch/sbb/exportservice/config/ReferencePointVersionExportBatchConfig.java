package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.model.PrmBatchExportFileName.REFERENCE_POINT_VERSION;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.ReferencePointVersionCsvProcessor;
import ch.sbb.exportservice.processor.ReferencePointVersionJsonProcessor;
import ch.sbb.exportservice.reader.ReferencePointVersionRowMapper;
import ch.sbb.exportservice.reader.ReferencePointVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvReferencePointVersionWriter;
import ch.sbb.exportservice.writer.JsonReferencePointVersionWriter;
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
public class ReferencePointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvReferencePointVersionWriter csvReferencePointVersionWriter;
  private final JsonReferencePointVersionWriter jsonReferencePointVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ReferencePointVersion> referencePointReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    JdbcCursorItemReader<ReferencePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ReferencePointVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ReferencePointVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportReferencePointCsvStep(ItemReader<ReferencePointVersion> itemReader) {
    final String stepName = "exportReferencePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ReferencePointVersion, ReferencePointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(referencePointVersionCsvProcessor())
        .writer(referencePointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ReferencePointVersionCsvProcessor referencePointVersionCsvProcessor() {
    return new ReferencePointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ReferencePointVersionCsvModel> referencePointCsvWriter(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return csvReferencePointVersionWriter.csvWriter(exportType, REFERENCE_POINT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_REFERENCE_POINT_CSV_JOB_NAME)
  public Job exportReferencePointCsvJob(ItemReader<ReferencePointVersion> itemReader) {
    return new JobBuilder(EXPORT_REFERENCE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportReferencePointCsvStep(itemReader))
        .next(uploadReferencePointCsvFileStep())
        .next(deleteReferencePointCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadReferencePointCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadReferencePointCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadReferencePointCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return new UploadCsvFileTasklet(exportType, REFERENCE_POINT_VERSION);
  }

  @Bean
  public Step deleteReferencePointCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(referencePointCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileCsvDeletingTasklet referencePointCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return new FileCsvDeletingTasklet(exportType, REFERENCE_POINT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_REFERENCE_POINT_JSON_JOB_NAME)
  public Job exportReferencePointJsonJob(ItemReader<ReferencePointVersion> itemReader) {
    return new JobBuilder(EXPORT_REFERENCE_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportReferencePointJsonStep(itemReader))
        .next(uploadReferencePointJsonFileStep())
        .next(deleteReferencePointJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadReferencePointJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadReferencePointJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadReferencePointJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return new UploadJsonFileTasklet(exportType, REFERENCE_POINT_VERSION);
  }

  @Bean
  public Step deleteReferencePointJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileReferencePointJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileJsonDeletingTasklet fileReferencePointJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return new FileJsonDeletingTasklet(exportType, REFERENCE_POINT_VERSION);
  }

  @Bean
  public Step exportReferencePointJsonStep(ItemReader<ReferencePointVersion> itemReader) {
    String stepName = "exportReferencePointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ReferencePointVersion, ReadReferencePointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(referencePointVersionJsonProcessor())
        .writer(referencePointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ReferencePointVersionJsonProcessor referencePointVersionJsonProcessor() {
    return new ReferencePointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadReferencePointVersionModel> referencePointJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return jsonReferencePointVersionWriter.getWriter(exportType, REFERENCE_POINT_VERSION);
  }

}
