package ch.sbb.exportservice.job.sepodi.loadingpoint;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
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
public class LoadingPointVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvLoadingPointVersionWriter csvLoadingPointVersionWriter;
  private final JsonLoadingPointVersionWriter jsonLoadingPointVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<LoadingPointVersion> loadingPointReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<LoadingPointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(LoadingPointVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new LoadingPointVersionRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME)
  public Job exportLoadingPointCsvJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLoadingPointCsvStep(itemReader))
        .next(uploadLoadingPointCsvFileStepV2())
        .next(renameLoadingPointCsvStep())
        .next(uploadLoadingPointCsvFileStepV1())
        .next(deleteLoadingPointCsvFileStepV2())
        .next(deleteLoadingPointCsvFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportLoadingPointCsvStep(ItemReader<LoadingPointVersion> itemReader) {
    final String stepName = "exportLoadingPointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<LoadingPointVersion, LoadingPointVersionCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(loadingPointVersionCsvProcessor())
        .writer(loadingPointCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LoadingPointVersionCsvProcessor loadingPointVersionCsvProcessor() {
    return new LoadingPointVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<LoadingPointVersionCsvModel> loadingPointCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvLoadingPointVersionWriter.csvWriter(ExportObjectV2.LOADING_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadLoadingPointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadLoadingPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadLoadingPointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadLoadingPointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadLoadingPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadLoadingPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameLoadingPointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameLoadingPointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameLoadingPointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteLoadingPointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteLoadingPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteLoadingPointCsvFileTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteLoadingPointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteLoadingPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteLoadingPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME)
  public Job exportLoadingPointJsonJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLoadingPointJsonStep(itemReader))
        .next(uploadLoadingPointJsonFileStepV2())
        .next(renameLoadingPointJsonStep())
        .next(uploadLoadingPointJsonFileStepV1())
        .next(deleteLoadingPointJsonFileStepV2())
        .next(deleteLoadingPointJsonFileStepV1())
        .end()
        .build();
  }

  @Bean
  public Step exportLoadingPointJsonStep(ItemReader<LoadingPointVersion> itemReader) {
    String stepName = "exportLoadingPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<LoadingPointVersion, ReadLoadingPointVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(loadingPointVersionJsonProcessor())
        .writer(loadingPointJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LoadingPointVersionJsonProcessor loadingPointVersionJsonProcessor() {
    return new LoadingPointVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadLoadingPointVersionModel> loadingPointJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonLoadingPointVersionWriter.getWriter(ExportObjectV2.LOADING_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadLoadingPointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadLoadingPointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadLoadingPointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.LOADING_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameLoadingPointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameLoadingPointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameLoadingPointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadLoadingPointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadLoadingPointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadLoadingPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteLoadingPointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteLoadingPointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteLoadingPointJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteLoadingPointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteLoadingPointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteLoadingPointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
  }
  // END: Delete Json V1

}
