package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.model.PrmBatchExportFileName.PARKING_LOT_VERSION;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.entity.ParkingLotVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.ParkingLotVersionCsvProcessor;
import ch.sbb.exportservice.processor.ParkingLotVersionJsonProcessor;
import ch.sbb.exportservice.reader.ParkingLotVersionRowMapper;
import ch.sbb.exportservice.reader.ParkingLotVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvParkingLotVersionWriter;
import ch.sbb.exportservice.writer.JsonParkingLotVersionWriter;
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
public class ParkingLotVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvParkingLotVersionWriter csvParkingLotVersionWriter;
  private final JsonParkingLotVersionWriter jsonParkingLotVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ParkingLotVersion> parkingLotReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    JdbcCursorItemReader<ParkingLotVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ParkingLotVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ParkingLotVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportParkingLotCsvStep(ItemReader<ParkingLotVersion> itemReader) {
    final String stepName = "exportParkingLotCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ParkingLotVersion, ParkingLotVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(parkingLotVersionCsvProcessor())
        .writer(parkingLotCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ParkingLotVersionCsvProcessor parkingLotVersionCsvProcessor() {
    return new ParkingLotVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ParkingLotVersionCsvModel> parkingLotCsvWriter(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return csvParkingLotVersionWriter.csvWriter(exportType, PARKING_LOT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_PARKING_LOT_CSV_JOB_NAME)
  public Job exportParkingLotCsvJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotCsvStep(itemReader))
        .next(uploadParkingLotCsvFileStep())
        .next(deleteParkingLotCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadParkingLotCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadParkingLotCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadParkingLotCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return new UploadCsvFileTasklet(exportType, PARKING_LOT_VERSION);
  }

  @Bean
  public Step deleteParkingLotCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(parkingLotCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileCsvDeletingTasklet parkingLotCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType
  ) {
    return new FileCsvDeletingTasklet(exportType, PARKING_LOT_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_PARKING_LOT_JSON_JOB_NAME)
  public Job exportParkingLotJsonJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotJsonStep(itemReader))
        .next(uploadParkingLotJsonFileStep())
        .next(deleteParkingLotJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadParkingLotJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadParkingLotJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadParkingLotJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return new UploadJsonFileTasklet(exportType, PARKING_LOT_VERSION);
  }

  @Bean
  public Step deleteParkingLotJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileParkingLotJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileJsonDeletingTasklet fileParkingLotJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return new FileJsonDeletingTasklet(exportType, PARKING_LOT_VERSION);
  }

  @Bean
  public Step exportParkingLotJsonStep(ItemReader<ParkingLotVersion> itemReader) {
    String stepName = "exportParkingLotJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ParkingLotVersion, ReadParkingLotVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(parkingLotVersionJsonProcessor())
        .writer(parkingLotJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ParkingLotVersionJsonProcessor parkingLotVersionJsonProcessor() {
    return new ParkingLotVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadParkingLotVersionModel> parkingLotJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
    return jsonParkingLotVersionWriter.getWriter(exportType, PARKING_LOT_VERSION);
  }

}
