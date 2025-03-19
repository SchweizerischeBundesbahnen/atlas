package ch.sbb.exportservice.job.sepodi.servicepoint;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.tasklet.RenameTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.utile.StepUtil;
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
public class ServicePointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final JsonServicePointVersionWriter jsonServicePointVersionWriter;
  private final CsvServicePointVersionWriter csvServicePointVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ServicePointVersion> reader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    JdbcCursorItemReader<ServicePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ServicePointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new ServicePointVersionRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  public Job exportServicePointCsvJob(ItemReader<ServicePointVersion> itemReader) {
    return new JobBuilder(EXPORT_SERVICE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointCsvStep(itemReader))
        .next(uploadServicePointCsvFileStepV2())
        .next(renameServicePointCsvStep())
        .next(uploadServicePointCsvFileStepV1())
        .next(deleteServicePointCsvFileStepV2())
        .next(deleteServicePointCsvFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportServicePointCsvStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersionCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionCsvProcessor())
        .writer(csvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ServicePointVersionCsvProcessor servicePointVersionCsvProcessor() {
    return new ServicePointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ServicePointVersionCsvModel> csvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvServicePointVersionWriter.csvWriter(ExportObjectV2.SERVICE_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadServicePointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadServicePointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadServicePointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadServicePointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadServicePointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadServicePointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameServicePointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameServicePointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameServicePointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteServicePointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteServicePointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteServicePointCsvFileTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteServicePointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteServicePointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteServicePointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME)
  public Job exportServicePointJsonJob(ItemReader<ServicePointVersion> itemReader) {
    return new JobBuilder(EXPORT_SERVICE_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointJsonStep(itemReader))
        .next(uploadServicePointJsonFileStepV2())
        .next(renameServicePointJsonStep())
        .next(uploadServicePointJsonFileStepV1())
        .next(deleteServicePointJsonFileStepV2())
        .next(deleteServicePointJsonFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportServicePointJsonStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ReadServicePointVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionJsonProcessor())
        .writer(jsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ServicePointVersionJsonProcessor servicePointVersionJsonProcessor() {
    return new ServicePointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadServicePointVersionModel> jsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonServicePointVersionWriter.getWriter(ExportObjectV2.SERVICE_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadServicePointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadServicePointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadServicePointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SERVICE_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameServicePointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameServicePointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameServicePointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadServicePointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadServicePointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadServicePointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteServicePointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteServicePointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteServicePointJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteServicePointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteServicePointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteServicePointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
  }
  // END: Delete Json V1

}
