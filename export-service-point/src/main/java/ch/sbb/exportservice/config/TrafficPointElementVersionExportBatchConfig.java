package ch.sbb.exportservice.config;

import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.processor.TrafficPointElementVersionCsvProcessor;
import ch.sbb.exportservice.processor.TrafficPointElementVersionJsonProcessor;
import ch.sbb.exportservice.reader.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.reader.TrafficPointElementVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvTrafficPointElementVersionWriter;
import ch.sbb.exportservice.writer.JsonTrafficPointElementVersionWriter;
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

import javax.sql.DataSource;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

@Configuration
@AllArgsConstructor
public class TrafficPointElementVersionExportBatchConfig {

  private final JobRepository jobRepository;

  private final PlatformTransactionManager transactionManager;

  private final JobCompletionListener jobCompletionListener;

  private final StepTracerListener stepTracerListener;

  private final JsonTrafficPointElementVersionWriter jsonTrafficPointElementVersionWriter;

  private final CsvTrafficPointElementVersionWriter csvTrafficPointElementVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<TrafficPointElementVersion> trafficPointElementReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    JdbcCursorItemReader<TrafficPointElementVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(TrafficPointElementVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new TrafficPointElementVersionRowMapper());
    return itemReader;
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadTrafficPointElementVersionModel> trafficPointElementJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonTrafficPointElementVersionWriter.getWriter(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<TrafficPointVersionCsvModel> trafficPointElementCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return csvTrafficPointElementVersionWriter.csvWriter(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  public TrafficPointElementVersionCsvProcessor trafficPointElementVersionCsvProcessor() {
    return new TrafficPointElementVersionCsvProcessor();
  }

  @Bean
  public TrafficPointElementVersionJsonProcessor trafficPointElementVersionJsonProcessor() {
    return new TrafficPointElementVersionJsonProcessor();
  }

  @Bean
  public Step exportTrafficPointElementCsvStep(ItemReader<TrafficPointElementVersion> itemReader) {
    String stepName = "exportTrafficPointElementCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<TrafficPointElementVersion, TrafficPointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(trafficPointElementVersionCsvProcessor())
        .writer(trafficPointElementCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step exportTrafficPointElementJsonStep(ItemReader<TrafficPointElementVersion> itemReader) {
    String stepName = "exportTrafficPointElementJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<TrafficPointElementVersion, ReadTrafficPointElementVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(trafficPointElementVersionJsonProcessor())
        .writer(trafficPointElementJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)
  public Job exportTrafficPointElementCsvJob(ItemReader<TrafficPointElementVersion> itemReader) {
    return new JobBuilder(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementCsvStep(itemReader))
        .next(uploadTrafficPointElementCsvFileStep())
        .next(deleteTrafficPointElementCsvFileStep())
        .end()
        .build();
  }

  @Bean
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME)
  public Job exportTrafficPointElementJsonJob(ItemReader<TrafficPointElementVersion> itemReader) {
    return new JobBuilder(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementJsonStep(itemReader))
        .next(uploadTrafficPointElementJsonFileStep())
        .next(deleteTrafficPointElementJsonFileStep())
        .end()
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadTrafficPointElementCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new UploadCsvFileTasklet(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadTrafficPointElementJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new UploadJsonFileTasklet(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  @StepScope
  public FileJsonDeletingTasklet fileTrafficPointElementJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new FileJsonDeletingTasklet(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  @StepScope
  public FileCsvDeletingTasklet fileTrafficPointElementCsvDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new FileCsvDeletingTasklet(exportType, BatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }

  @Bean
  public Step uploadTrafficPointElementCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadTrafficPointElementCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadTrafficPointElementJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadTrafficPointElementJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step deleteTrafficPointElementCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(fileTrafficPointElementCsvDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step deleteTrafficPointElementJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileTrafficPointElementJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

}
