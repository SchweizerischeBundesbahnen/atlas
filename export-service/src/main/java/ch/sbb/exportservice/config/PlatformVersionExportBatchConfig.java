package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.exportservice.entity.prm.PlatformVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportFilePath.ExportFilePathBuilder;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportObjectV1;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.processor.PlatformVersionCsvProcessor;
import ch.sbb.exportservice.processor.PlatformVersionJsonProcessor;
import ch.sbb.exportservice.reader.PlatformVersionRowMapper;
import ch.sbb.exportservice.reader.PlatformVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
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

  @Bean
  @StepScope
  public JdbcCursorItemReader<PlatformVersion> platformReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<PlatformVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(PlatformVersionSqlQueryUtil.getSqlQuery(exportType));
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
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvPlatformVersionWriter.csvWriter(ExportObject.PLATFORM, exportType);
  }

  @Bean
  @Qualifier(EXPORT_PLATFORM_CSV_JOB_NAME)
  public Job exportPlatformCsvJob(ItemReader<PlatformVersion> itemReader) {
    return new JobBuilder(EXPORT_PLATFORM_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportPlatformCsvStep(itemReader))
        .next(uploadPlatformCsvFileStep())
        .next(uploadPlatformCsvFileStepV1()) // todo: add this step to all other configs with V1 tasklet
        .next(deletePlatformCsvFileStep())
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
        .next(uploadPlatformJsonFileStep())
        .next(uploadPlatformJsonFileStepV1()) // todo: add this step to all other configs with V1 tasklet
        .next(deletePlatformJsonFileStep())
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
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonPlatformVersionWriter.getWriter(ExportObject.PLATFORM, exportType);
  }

  @Bean
  public Step uploadPlatformCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadPlatformCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformCsvFileStepV1() {
    return new StepBuilder("uploadCsvFileV1", jobRepository)
        .tasklet(uploadPlatformCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadPlatformJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadPlatformJsonFileStepV1() {
    return new StepBuilder("uploadJsonFileV1", jobRepository)
        .tasklet(uploadPlatformJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
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
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadPlatformCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadPlatformCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.PLATFORM_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadPlatformJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadPlatformJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.PLATFORM_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet filePlatformCsvDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet filePlatformJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.PLATFORM, exportType);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }
}
