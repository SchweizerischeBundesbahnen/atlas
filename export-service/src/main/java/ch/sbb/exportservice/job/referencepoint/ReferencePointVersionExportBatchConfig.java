package ch.sbb.exportservice.job.referencepoint;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
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
import ch.sbb.exportservice.utils.StepUtils;
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

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ReferencePointVersion> referencePointReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<ReferencePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ReferencePointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ReferencePointVersionRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_REFERENCE_POINT_CSV_JOB_NAME)
  public Job exportReferencePointCsvJob(ItemReader<ReferencePointVersion> itemReader) {
    return new JobBuilder(EXPORT_REFERENCE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportReferencePointCsvStep(itemReader))
        .next(uploadReferencePointCsvFileStepV2())
        .next(renameReferencePointCsvStep())
        .next(uploadReferencePointCsvFileStepV1())
        .next(deleteReferencePointCsvFileStepV2())
        .next(deleteReferencePointCsvFileStepV1())
        .end()
        .build();
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvReferencePointVersionWriter.csvWriter(ExportObjectV2.REFERENCE_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadReferencePointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadReferencePointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadReferencePointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.REFERENCE_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadReferencePointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadReferencePointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadReferencePointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameReferencePointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameReferencePointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameReferencePointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteReferencePointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteReferencePointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteReferencePointCsvFileTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteReferencePointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteReferencePointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteReferencePointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_REFERENCE_POINT_JSON_JOB_NAME)
  public Job exportReferencePointJsonJob(ItemReader<ReferencePointVersion> itemReader) {
    return new JobBuilder(EXPORT_REFERENCE_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportReferencePointJsonStep(itemReader))
        .next(uploadReferencePointJsonFileStepV2())
        .next(renameReferencePointJsonStep())
        .next(uploadReferencePointJsonFileStepV1())
        .next(deleteReferencePointJsonFileStepV2())
        .next(deleteReferencePointJsonFileStepV1())
        .end()
        .build();
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonReferencePointVersionWriter.getWriter(ExportObjectV2.REFERENCE_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadReferencePointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadReferencePointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadReferencePointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.REFERENCE_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameReferencePointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameReferencePointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameReferencePointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadReferencePointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadReferencePointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadReferencePointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteReferencePointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteReferencePointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteReferencePointJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteReferencePointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteReferencePointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteReferencePointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.REFERENCE_POINT_VERSION);
  }
  // END: Delete Json V1

}
