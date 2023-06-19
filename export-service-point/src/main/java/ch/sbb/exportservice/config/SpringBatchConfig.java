package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.processor.ServicePointVersionCsvProcessor;
import ch.sbb.exportservice.processor.ServicePointVersionJsonProcessor;
import ch.sbb.exportservice.reader.BaseServicePointVersionReader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.SqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvServicePointWriter;
import ch.sbb.exportservice.writer.JsonServicePointWriter;
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
public class SpringBatchConfig {

  private static final int CHUNK_SIZE = 200;
  private final JobRepository jobRepository;

  private final PlatformTransactionManager transactionManager;

  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  private final JsonServicePointWriter jsonServicePointWriter;

  private final CsvServicePointWriter csvServicePointWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ServicePointVersion> reader(@Autowired @Qualifier("servicePointDataSource") DataSource dataSource
      , @Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    JdbcCursorItemReader<ServicePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(10000);
    itemReader.setRowMapper(new ServicePointVersionRowMapper());
    return itemReader;
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ServicePointVersionModel> jsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return jsonServicePointWriter.getWriter(exportType);
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ServicePointVersionCsvModel> csvWriter(
      @Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return csvServicePointWriter.csvWriter(exportType);
  }

  @Bean
  public ServicePointVersionCsvProcessor servicePointVersionCsvProcessor() {
    return new ServicePointVersionCsvProcessor();
  }

  @Bean
  public ServicePointVersionJsonProcessor servicePointVersionJsonProcessor() {
    return new ServicePointVersionJsonProcessor();
  }

  @Bean
  public Step exportServicePointCsvStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersionCsvModel>chunk(CHUNK_SIZE, transactionManager)
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
        .<ServicePointVersion, ServicePointVersionModel>chunk(CHUNK_SIZE, transactionManager)
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
        .next(uploadCsvFileStep())
        .next(deleteCsvFileStep())
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
        .next(uploadJsonFileStep())
        .next(deleteJsonFileStep())
        .end()
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadCsvFileTasklet(@Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return new UploadCsvFileTasklet(exportType);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadJsonFileTasklet(@Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return new UploadJsonFileTasklet(exportType);
  }

  @Bean
  @StepScope
  public FileJsonDeletingTasklet fileJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return new FileJsonDeletingTasklet(exportType);
  }

  @Bean
  @StepScope
  public FileCsvDeletingTasklet fileCsvDeletingTasklet(@Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    return new FileCsvDeletingTasklet(exportType);
  }

  @Bean
  public Step uploadCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadCsvFileTasklet(null), transactionManager)
        .build();
  }

  @Bean
  public Step uploadJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadJsonFileTasklet(null), transactionManager)
        .build();
  }

  @Bean
  public Step deleteCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(fileCsvDeletingTasklet(null), transactionManager)
        .build();
  }

  @Bean
  public Step deleteJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileJsonDeletingTasklet(null), transactionManager)
        .build();
  }

}
