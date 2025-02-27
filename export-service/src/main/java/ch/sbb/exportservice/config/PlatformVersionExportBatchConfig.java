package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.exportservice.entity.prm.PlatformVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.PlatformVersionCsvProcessor;
import ch.sbb.exportservice.processor.PlatformVersionJsonProcessor;
import ch.sbb.exportservice.reader.PlatformVersionRowMapper;
import ch.sbb.exportservice.reader.PlatformVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTasklet;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvPlatformVersionWriter;
import ch.sbb.exportservice.writer.JsonPlatformVersionWriter;
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
public class PlatformVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvPlatformVersionWriter csvPlatformVersionWriter;
  private final JsonPlatformVersionWriter jsonPlatformVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<PlatformVersion> platformReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<PlatformVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(PlatformVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new PlatformVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportPlatformCsvStep(ItemReader<PlatformVersion> itemReader) {
    final String stepName = "exportPlatformCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<PlatformVersion, PlatformVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(platformVersionCsvProcessor())
        .writer(platformCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public PlatformVersionCsvProcessor platformVersionCsvProcessor() {
    return new PlatformVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<PlatformVersionCsvModel> platformCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvPlatformVersionWriter.csvWriter(ExportObjectV2.PLATFORM, exportTypeV2);
  }

  @Bean
  @Qualifier(EXPORT_PLATFORM_CSV_JOB_NAME)
  public Job exportPlatformCsvJob(ItemReader<PlatformVersion> itemReader) {
    return new JobBuilder(EXPORT_PLATFORM_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportPlatformCsvStep(itemReader))
        .next(uploadPlatformCsvFileStepV2())
        // todo: copy
        .next(uploadPlatformCsvFileStepV1()) // todo: add this step to all other configs with V1 tasklet
        //.next(deletePlatformCsvFileStep())
        .end()
        .build();
  }

  @Bean
  @Qualifier(EXPORT_PLATFORM_JSON_JOB_NAME)
  public Job exportPlatformJsonJob(ItemReader<PlatformVersion> itemReader) {
    return new JobBuilder(EXPORT_PLATFORM_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportPlatformJsonStep(itemReader))
        .next(uploadPlatformJsonFileStepV2())
        // todo: copy
        .next(uploadPlatformJsonFileStepV1()) // todo: add this step to all other configs with V1 tasklet
        //.next(deletePlatformJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportPlatformJsonStep(ItemReader<PlatformVersion> itemReader) {
    String stepName = "exportPlatformJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<PlatformVersion, ReadPlatformVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(platformVersionJsonProcessor())
        .writer(platformJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public PlatformVersionJsonProcessor platformVersionJsonProcessor() {
    return new PlatformVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadPlatformVersionModel> platformJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonPlatformVersionWriter.getWriter(ExportObjectV2.PLATFORM, exportTypeV2);
  }

  @Bean
  public Step uploadPlatformCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadPlatformCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadPlatformCsvFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformJsonFileStepV2() {
    return new StepBuilder("uploadJsonFileV2", jobRepository)
        .tasklet(uploadPlatformJsonFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadPlatformJsonFileTaskletV1(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  /*@Bean
  public Step deletePlatformCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(filePlatformCsvDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step deletePlatformJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(filePlatformJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }*/

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadPlatformCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.PLATFORM, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadPlatformCsvFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadCsvFileTasklet(exportTypeV1, PrmBatchExportFileName.PLATFORM_VERSION);
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadPlatformJsonFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.PLATFORM, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadPlatformJsonFileTaskletV1(
      @Value("#{jobParameters[exportTypeV1]}") PrmExportType exportTypeV1
  ) {
    return new UploadJsonFileTasklet(exportTypeV1, PrmBatchExportFileName.PLATFORM_VERSION);
  }

  /*@Bean
  @StepScope
  public DeleteCsvFileTasklet filePlatformCsvDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PLATFORM, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet filePlatformJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.PLATFORM, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }*/
}
