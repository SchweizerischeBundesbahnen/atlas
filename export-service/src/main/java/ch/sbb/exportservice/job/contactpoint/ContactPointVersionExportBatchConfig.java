package ch.sbb.exportservice.job.contactpoint;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
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
public class ContactPointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvContactPointVersionWriter csvContactPointVersionWriter;
  private final JsonContactPointVersionWriter jsonContactPointVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ContactPointVersion> contactPointReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<ContactPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ContactPointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ContactPointVersionRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_CONTACT_POINT_CSV_JOB_NAME)
  public Job exportContactPointCsvJob(ItemReader<ContactPointVersion> itemReader) {
    return new JobBuilder(EXPORT_CONTACT_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportContactPointCsvStep(itemReader))
        .next(uploadContactPointCsvFileStepV2())
        .next(renameContactPointCsvStep())
        .next(uploadContactPointCsvFileStepV1())
        .next(deleteContactPointCsvFileStepV2())
        .next(deleteContactPointCsvFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportContactPointCsvStep(ItemReader<ContactPointVersion> itemReader) {
    final String stepName = "exportContactPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ContactPointVersion, ContactPointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(contactPointVersionCsvProcessor())
        .writer(contactPointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ContactPointVersionCsvProcessor contactPointVersionCsvProcessor() {
    return new ContactPointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ContactPointVersionCsvModel> contactPointCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvContactPointVersionWriter.csvWriter(ExportObjectV2.CONTACT_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadContactPointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadContactPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadContactPointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.CONTACT_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadContactPointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadContactPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadContactPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameContactPointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameContactPointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameContactPointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteContactPointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteContactPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteContactPointCsvFileTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteContactPointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteContactPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteContactPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_CONTACT_POINT_JSON_JOB_NAME)
  public Job exportContactPointJsonJob(ItemReader<ContactPointVersion> itemReader) {
    return new JobBuilder(EXPORT_CONTACT_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportContactPointJsonStep(itemReader))
        .next(uploadContactPointJsonFileStepV2())
        .next(renameContactPointJsonStep())
        .next(uploadContactPointJsonFileStepV1())
        .next(deleteContactPointJsonFileStepV2())
        .next(deleteContactPointJsonFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportContactPointJsonStep(ItemReader<ContactPointVersion> itemReader) {
    String stepName = "exportContactPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ContactPointVersion, ReadContactPointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(contactPointVersionJsonProcessor())
        .writer(contactPointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ContactPointVersionJsonProcessor contactPointVersionJsonProcessor() {
    return new ContactPointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadContactPointVersionModel> contactPointJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonContactPointVersionWriter.getWriter(ExportObjectV2.CONTACT_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadContactPointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadContactPointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadContactPointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.CONTACT_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameContactPointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameContactPointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameContactPointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadContactPointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadContactPointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadContactPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteContactPointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteContactPointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteContactPointJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteContactPointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteContactPointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteContactPointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.CONTACT_POINT_VERSION);
  }
  // END: Delete Json V1

}
