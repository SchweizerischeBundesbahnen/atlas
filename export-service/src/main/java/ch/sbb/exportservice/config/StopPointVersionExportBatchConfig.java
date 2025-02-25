package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.exportservice.entity.prm.StopPointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportFilePath.ExportFilePathBuilder;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportObjectV1;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.processor.StopPointVersionCsvProcessor;
import ch.sbb.exportservice.processor.StopPointVersionJsonProcessor;
import ch.sbb.exportservice.reader.StopPointVersionRowMapper;
import ch.sbb.exportservice.reader.StopPointVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvStopPointVersionWriter;
import ch.sbb.exportservice.writer.JsonStopPointVersionWriter;
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
public class StopPointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvStopPointVersionWriter csvStopPointVersionWriter;
  private final JsonStopPointVersionWriter jsonStopPointVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<StopPointVersion> stopPointReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<StopPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(StopPointVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new StopPointVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportStopPointCsvStep(ItemReader<StopPointVersion> itemReader) {
    final String stepName = "exportStopPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPointVersion, StopPointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(stopPointVersionCsvProcessor())
        .writer(stopPointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public StopPointVersionCsvProcessor stopPointVersionCsvProcessor() {
    return new StopPointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<StopPointVersionCsvModel> stopPointCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvStopPointVersionWriter.csvWriter(ExportObject.STOP_POINT, exportType);
  }

  @Bean
  @Qualifier(EXPORT_STOP_POINT_CSV_JOB_NAME)
  public Job exportStopPointCsvJob(ItemReader<StopPointVersion> itemReader) {
    return new JobBuilder(EXPORT_STOP_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportStopPointCsvStep(itemReader))
        .next(uploadStopPointCsvFileStep())
        .next(deleteStopPointCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadStopPointCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadStopPointCsvFileTasklet(null), transactionManager)
        .tasklet(uploadStopPointCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadStopPointCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadStopPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.STOP_POINT_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteStopPointCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(stopPointCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet stopPointCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @Qualifier(EXPORT_STOP_POINT_JSON_JOB_NAME)
  public Job exportStopPointJsonJob(ItemReader<StopPointVersion> itemReader) {
    return new JobBuilder(EXPORT_STOP_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportStopPointJsonStep(itemReader))
        .next(uploadStopPointJsonFileStep())
        .next(deleteStopPointJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadStopPointJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadStopPointJsonFileTasklet(null), transactionManager)
        .tasklet(uploadStopPointJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadStopPointJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadStopPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.STOP_POINT_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteStopPointJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileStopPointJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileStopPointJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.STOP_POINT, exportType);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  public Step exportStopPointJsonStep(ItemReader<StopPointVersion> itemReader) {
    String stepName = "exportStopPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPointVersion, ReadStopPointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(stopPointVersionJsonProcessor())
        .writer(stopPointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public StopPointVersionJsonProcessor stopPointVersionJsonProcessor() {
    return new StopPointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadStopPointVersionModel> stopPointJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonStopPointVersionWriter.getWriter(ExportObject.STOP_POINT, exportType);
  }

}
