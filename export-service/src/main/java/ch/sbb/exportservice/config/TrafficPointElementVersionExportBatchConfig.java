package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.exportservice.entity.sepodi.TrafficPointElementVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.processor.TrafficPointElementVersionCsvProcessor;
import ch.sbb.exportservice.processor.TrafficPointElementVersionJsonProcessor;
import ch.sbb.exportservice.reader.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.reader.TrafficPointElementVersionSqlQueryUtil;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvTrafficPointElementVersionWriter;
import ch.sbb.exportservice.writer.JsonTrafficPointElementVersionWriter;
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    JdbcCursorItemReader<TrafficPointElementVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(TrafficPointElementVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new TrafficPointElementVersionRowMapper());
    itemReader.close();
    return itemReader;
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadTrafficPointElementVersionModel> trafficPointElementJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonTrafficPointElementVersionWriter.getWriter(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<TrafficPointVersionCsvModel> trafficPointElementCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvTrafficPointElementVersionWriter.csvWriter(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
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
        //        .next(uploadTrafficPointElementCsvFileStep())
        //        .next(deleteTrafficPointElementCsvFileStep())
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
        //        .next(uploadTrafficPointElementJsonFileStep())
        //        .next(deleteTrafficPointElementJsonFileStep())
        .end()
        .build();
  }

  /*@Bean
  @StepScope
  public UploadCsvFileTasklet uploadTrafficPointElementCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadTrafficPointElementCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.TRAFFIC_POINT_ELEMENT_VERSION,
        exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadTrafficPointElementJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadTrafficPointElementJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
    final ExportFilePathBuilder s3FIle = ExportFilePathV1.getV1Builder(ExportObjectV1.TRAFFIC_POINT_ELEMENT_VERSION,
        exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3FIle);
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileTrafficPointElementJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet fileTrafficPointElementCsvDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
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
  }*/

}
