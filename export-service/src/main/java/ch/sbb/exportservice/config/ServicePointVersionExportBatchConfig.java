package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.exportservice.entity.sepodi.ServicePointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.processor.ServicePointVersionCsvServicePointProcessor;
import ch.sbb.exportservice.processor.ServicePointVersionJsonServicePointProcessor;
import ch.sbb.exportservice.reader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.ServicePointVersionSqlQueryUtil;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvServicePointVersionWriter;
import ch.sbb.exportservice.writer.JsonServicePointVersionWriter;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ServicePointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final JsonServicePointVersionWriter jsonServicePointVersionWriter;
  private final CsvServicePointVersionWriter csvServicePointVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ServicePointVersion> reader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    JdbcCursorItemReader<ServicePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ServicePointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ServicePointVersionRowMapper());
    return itemReader;
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadServicePointVersionModel> jsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonServicePointVersionWriter.getWriter(ExportObjectV2.SERVICE_POINT, exportTypeV2);
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ServicePointVersionCsvModel> csvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvServicePointVersionWriter.csvWriter(ExportObjectV2.SERVICE_POINT, exportTypeV2);
  }

  @Bean
  public ServicePointVersionCsvServicePointProcessor servicePointVersionCsvProcessor() {
    return new ServicePointVersionCsvServicePointProcessor();
  }

  @Bean
  public ServicePointVersionJsonServicePointProcessor servicePointVersionJsonProcessor() {
    return new ServicePointVersionJsonServicePointProcessor();
  }

  @Bean
  public Step exportServicePointCsvStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionCsvProcessor())
        .writer(csvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step exportServicePointJsonStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ReadServicePointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionJsonProcessor())
        .writer(jsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  public Job exportServicePointCsvJob(ItemReader<ServicePointVersion> itemReader) {
    return new JobBuilder(EXPORT_SERVICE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointCsvStep(itemReader))
        //        .next(uploadCsvFileStep())
        //        .next(deleteCsvFileStep())
        .end()
        .build();
  }

  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME)
  public Job exportServicePointJsonJob(ItemReader<ServicePointVersion> itemReader) {
    return new JobBuilder(EXPORT_SERVICE_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointJsonStep(itemReader))
        //        .next(uploadJsonFileStep())
        //        .next(deleteJsonFileStep())
        .end()
        .build();
  }

  /*@Bean
  @StepScope
  public UploadCsvFileTasklet uploadCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.SERVICE_POINT_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.SERVICE_POINT_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  @StepScope
  @Qualifier("fileCsvDeletingTasklet")
  public DeleteCsvFileTasklet fileCsvDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  public Step uploadCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadCsvFileTasklet(null), transactionManager)
        .tasklet(uploadCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadJsonFileTasklet(null), transactionManager)
        .tasklet(uploadJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step deleteCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(fileCsvDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step deleteJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }*/

}
