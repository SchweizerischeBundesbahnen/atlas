package ch.sbb.exportservice.config;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.entity.prm.ParkingLotVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.ParkingLotVersionCsvProcessor;
import ch.sbb.exportservice.processor.ParkingLotVersionJsonProcessor;
import ch.sbb.exportservice.reader.ParkingLotVersionRowMapper;
import ch.sbb.exportservice.reader.ParkingLotVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.RenameTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.delete.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteJsonFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_JSON_JOB_NAME;
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

  private final FileService fileService;

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

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_PARKING_LOT_CSV_JOB_NAME)
  public Job exportParkingLotCsvJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotCsvStep(itemReader))
        .next(uploadParkingLotCsvFileStepV2())
        .next(renameParkingLotCsvStep())
        .next(uploadParkingLotCsvFileStepV1())
        .next(deleteParkingLotCsvFileStepV2())
        .next(deleteParkingLotCsvFileStepV1())
        .end()
        .build();
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

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadParkingLotCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadParkingLotCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadParkingLotCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Bean
  public Step uploadParkingLotCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadParkingLotCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadParkingLotCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Bean
  public Step renameParkingLotCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameParkingLotCsvTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public RenameTasklet renameParkingLotCsvTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteParkingLotCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteParkingLotCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTaskletV2 deleteParkingLotCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new DeleteCsvFileTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Bean
  public Step deleteParkingLotCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteParkingLotCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteParkingLotCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_PARKING_LOT_JSON_JOB_NAME)
  public Job exportParkingLotJsonJob(ItemReader<ParkingLotVersion> itemReader) {
    return new JobBuilder(EXPORT_PARKING_LOT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportParkingLotJsonStep(itemReader))
        .next(uploadParkingLotJsonFileStepV2())
        .next(renameParkingLotJsonStep())
        .next(uploadParkingLotJsonFileStepV1())
        .next(deleteParkingLotJsonFileStepV2())
        .next(deleteParkingLotJsonFileStepV1())
        .end()
        .build();
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonParkingLotVersionWriter.getWriter(ExportObjectV2.PARKING_LOT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadParkingLotJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadParkingLotJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadParkingLotJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Bean
  public Step renameParkingLotJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameParkingLotJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public RenameTasklet renameParkingLotJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Bean
  public Step uploadParkingLotJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadParkingLotJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadParkingLotJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteParkingLotJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteParkingLotJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTaskletV2 deleteParkingLotJsonTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.PARKING_LOT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new DeleteJsonFileTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Bean
  public Step deleteParkingLotJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteParkingLotJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteParkingLotJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.PARKING_LOT_VERSION);
  }
  // END: Delete Json V1

}
