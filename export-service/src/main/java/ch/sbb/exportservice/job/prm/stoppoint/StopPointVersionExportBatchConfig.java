package ch.sbb.exportservice.job.prm.stoppoint;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.tasklet.RenameTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.util.StepUtil;
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

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<StopPointVersion> stopPointReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<StopPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(StopPointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new StopPointVersionRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_STOP_POINT_CSV_JOB_NAME)
  public Job exportStopPointCsvJob(ItemReader<StopPointVersion> itemReader) {
    return new JobBuilder(EXPORT_STOP_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportStopPointCsvStep(itemReader))
        .next(uploadStopPointCsvFileStepV2())
        .next(renameStopPointCsvStep())
        .next(uploadStopPointCsvFileStepV1())
        .next(deleteStopPointCsvFileStepV2())
        .next(deleteStopPointCsvFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportStopPointCsvStep(ItemReader<StopPointVersion> itemReader) {
    final String stepName = "exportStopPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPointVersion, StopPointVersionCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(stopPointVersionCsvProcessor())
        .writer(stopPointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvStopPointVersionWriter.csvWriter(ExportObjectV2.STOP_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadStopPointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadStopPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadStopPointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.STOP_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadStopPointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadStopPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadStopPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameStopPointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameStopPointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameStopPointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteStopPointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteStopPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteStopPointCsvFileTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteStopPointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteStopPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteStopPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_STOP_POINT_JSON_JOB_NAME)
  public Job exportStopPointJsonJob(ItemReader<StopPointVersion> itemReader) {
    return new JobBuilder(EXPORT_STOP_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportStopPointJsonStep(itemReader))
        .next(uploadStopPointJsonFileStepV2())
        .next(renameStopPointJsonStep())
        .next(uploadStopPointJsonFileStepV1())
        .next(deleteStopPointJsonFileStepV2())
        .next(deleteStopPointJsonFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportStopPointJsonStep(ItemReader<StopPointVersion> itemReader) {
    String stepName = "exportStopPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<StopPointVersion, ReadStopPointVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(stopPointVersionJsonProcessor())
        .writer(stopPointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonStopPointVersionWriter.getWriter(ExportObjectV2.STOP_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadStopPointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadStopPointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadStopPointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.STOP_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameStopPointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameStopPointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameStopPointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadStopPointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadStopPointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadStopPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteStopPointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteStopPointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteStopPointJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteStopPointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteStopPointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteStopPointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.STOP_POINT_VERSION);
  }
  // END: Delete Json V1

}
