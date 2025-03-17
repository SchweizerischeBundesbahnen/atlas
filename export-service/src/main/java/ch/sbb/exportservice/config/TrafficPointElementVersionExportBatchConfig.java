package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.exportservice.entity.sepodi.TrafficPointElementVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV1;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.processor.TrafficPointElementVersionCsvProcessor;
import ch.sbb.exportservice.processor.TrafficPointElementVersionJsonProcessor;
import ch.sbb.exportservice.reader.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.reader.TrafficPointElementVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.RenameTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.delete.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvTrafficPointElementVersionWriter;
import ch.sbb.exportservice.writer.JsonTrafficPointElementVersionWriter;
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
public class TrafficPointElementVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final JsonTrafficPointElementVersionWriter jsonTrafficPointElementVersionWriter;
  private final CsvTrafficPointElementVersionWriter csvTrafficPointElementVersionWriter;

  private final FileService fileService;

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

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)
  public Job exportTrafficPointElementCsvJob(ItemReader<TrafficPointElementVersion> itemReader) {
    return new JobBuilder(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementCsvStep(itemReader))
        .next(uploadTrafficPointCsvFileStepV2())
        .next(renameTrafficPointCsvStep())
        .next(uploadTrafficPointCsvFileStepV1())
        .next(deleteTrafficPointCsvFileStepV2())
        .next(deleteTrafficPointCsvFileStepV1())
        .end()
        .build();
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
  public TrafficPointElementVersionCsvProcessor trafficPointElementVersionCsvProcessor() {
    return new TrafficPointElementVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<TrafficPointVersionCsvModel> trafficPointElementCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvTrafficPointElementVersionWriter.csvWriter(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadTrafficPointCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadTrafficPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadTrafficPointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }
  // END: Upload Csv V2

  // BEGIN: Upload Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadTrafficPointCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadTrafficPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadTrafficPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }
  // END: Upload Csv V1

  // BEGIN: Rename Csv
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameTrafficPointCsvStep() {
    return new StepBuilder("renameCsv", jobRepository)
        .tasklet(renameTrafficPointTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameTrafficPointTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1,
        SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
        fileService.getDir(), ExportExtensionFileType.CSV_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Csv

  // BEGIN: Delete Csv V2
  @Bean
  public Step deleteTrafficPointCsvFileStepV2() {
    return new StepBuilder("deleteCsvFileV2", jobRepository)
        .tasklet(deleteTrafficPointCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTrafficPointCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv V2

  // BEGIN: Delete Csv V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteTrafficPointCsvFileStepV1() {
    return new StepBuilder("deleteCsvFileV1", jobRepository)
        .tasklet(deleteTrafficPointCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteTrafficPointCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new DeleteCsvFileTasklet(exportTypeV1, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }
  // END: Delete Csv V1

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME)
  public Job exportTrafficPointElementJsonJob(ItemReader<TrafficPointElementVersion> itemReader) {
    return new JobBuilder(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementJsonStep(itemReader))
        .next(uploadTrafficPointJsonFileStepV2())
        .next(renameTrafficPointJsonStep())
        .next(uploadTrafficPointJsonFileStepV1())
        .next(deleteTrafficPointJsonFileStepV2())
        .next(deleteTrafficPointJsonFileStepV1())
        .end()
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
  public TrafficPointElementVersionJsonProcessor trafficPointElementVersionJsonProcessor() {
    return new TrafficPointElementVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadTrafficPointElementVersionModel> trafficPointElementJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonTrafficPointElementVersionWriter.getWriter(ExportObjectV2.TRAFFIC_POINT, exportTypeV2);
  }

  // BEGIN: Upload Json V2 ---
  @Bean
  public Step uploadTrafficPointJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadTrafficPointJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadTrafficPointJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }
  // END: Upload Json V2

  // BEGIN: Rename Json
  @Deprecated(forRemoval = true)
  @Bean
  public Step renameTrafficPointJsonStep() {
    return new StepBuilder("renameJson", jobRepository)
        .tasklet(renameTrafficPointJsonTasklet(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public RenameTasklet renameTrafficPointJsonTasklet(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    final ExportFilePathV1 filePathV1 = new ExportFilePathV1(exportTypeV1,
        SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
        fileService.getDir(), ExportExtensionFileType.JSON_EXTENSION);
    return new RenameTasklet(filePathV2, filePathV1);
  }
  // END: Rename Json

  // BEGIN: Upload Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step uploadTrafficPointJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadTrafficPointJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadTrafficPointJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }
  // END: Upload Json V1

  // BEGIN: Delete Json V2
  @Bean
  public Step deleteTrafficPointJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteTrafficPointJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTrafficPointJsonTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.TRAFFIC_POINT, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json V2

  // BEGIN: Delete Json V1
  @Deprecated(forRemoval = true)
  @Bean
  public Step deleteTrafficPointJsonFileStepV1() {
    return new StepBuilder("deleteJsonFileV1", jobRepository)
        .tasklet(deleteTrafficPointJsonTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Deprecated(forRemoval = true)
  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteTrafficPointJsonTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") SePoDiExportType exportTypeV1) {

    return new DeleteJsonFileTasklet(exportTypeV1, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
  }
  // END: Delete Json V1

}
