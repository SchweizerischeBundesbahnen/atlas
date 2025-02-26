package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.entity.prm.ParkingLotVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.processor.ParkingLotVersionCsvProcessor;
import ch.sbb.exportservice.processor.ParkingLotVersionJsonProcessor;
import ch.sbb.exportservice.reader.ParkingLotVersionRowMapper;
import ch.sbb.exportservice.reader.ParkingLotVersionSqlQueryUtil;
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<ParkingLotVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ParkingLotVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvParkingLotVersionWriter.csvWriter(ExportObjectV2.PARKING_LOT, exportTypeV2);
  }

  @Bean
  @Qualifier(EXPORT_PARKING_LOT_CSV_JOB_NAME)
  public Job exportParkingLotCsvJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotCsvStep(itemReader))
        //        .next(uploadParkingLotCsvFileStep())
        //        .next(deleteParkingLotCsvFileStep())
        .end()
        .build();
  }

  /*@Bean
  public Step uploadParkingLotCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadParkingLotCsvFileTasklet(null), transactionManager)
        .tasklet(uploadParkingLotCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadParkingLotCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadParkingLotCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.PARKING_LOT_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
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
  public DeleteCsvFileTasklet parkingLotCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }*/

  @Bean
  @Qualifier(EXPORT_PARKING_LOT_JSON_JOB_NAME)
  public Job exportParkingLotJsonJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotJsonStep(itemReader))
        //        .next(uploadParkingLotJsonFileStep())
        //        .next(deleteParkingLotJsonFileStep())
        .end()
        .build();
  }
/*
  @Bean
  public Step uploadParkingLotJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadParkingLotJsonFileTasklet(null), transactionManager)
        .tasklet(uploadParkingLotJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadParkingLotJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadParkingLotJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.PARKING_LOT_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
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
  public DeleteJsonFileTasklet fileParkingLotJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }*/

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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonParkingLotVersionWriter.getWriter(ExportObjectV2.PARKING_LOT, exportTypeV2);
  }

}
