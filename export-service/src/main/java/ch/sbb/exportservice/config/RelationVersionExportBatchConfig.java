package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_RELATION_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_RELATION_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.exportservice.entity.RelationVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportFilePath.ExportFilePathBuilder;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportObjectV1;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
import ch.sbb.exportservice.processor.RelationVersionCsvProcessor;
import ch.sbb.exportservice.processor.RelationVersionJsonProcessor;
import ch.sbb.exportservice.reader.RelationVersionRowMapper;
import ch.sbb.exportservice.reader.RelationVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvRelationVersionWriter;
import ch.sbb.exportservice.writer.JsonRelationVersionWriter;
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
public class RelationVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvRelationVersionWriter csvRelationVersionWriter;
  private final JsonRelationVersionWriter jsonRelationVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<RelationVersion> relationReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<RelationVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(RelationVersionSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new RelationVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportRelationCsvStep(ItemReader<RelationVersion> itemReader) {
    final String stepName = "exportRelationCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<RelationVersion, RelationVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(relationVersionCsvProcessor())
        .writer(relationCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public RelationVersionCsvProcessor relationVersionCsvProcessor() {
    return new RelationVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<RelationVersionCsvModel> relationCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvRelationVersionWriter.csvWriter(ExportObject.RELATION, exportType);
  }

  @Bean
  @Qualifier(EXPORT_RELATION_CSV_JOB_NAME)
  public Job exportRelationCsvJob(ItemReader<RelationVersion> itemReader) {
    return new JobBuilder(EXPORT_RELATION_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportRelationCsvStep(itemReader))
        .next(uploadRelationCsvFileStep())
        .next(deleteRelationCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadRelationCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadRelationCsvFileTasklet(null), transactionManager)
        .tasklet(uploadRelationCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadRelationCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadRelationCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.RELATION_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteRelationCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(relationCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet relationCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @Qualifier(EXPORT_RELATION_JSON_JOB_NAME)
  public Job exportRelationJsonJob(ItemReader<RelationVersion> itemReader) {
    return new JobBuilder(EXPORT_RELATION_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportRelationJsonStep(itemReader))
        .next(uploadRelationJsonFileStep())
        .next(deleteRelationJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadRelationJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadRelationJsonFileTasklet(null), transactionManager)
        .tasklet(uploadRelationJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadRelationJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadRelationJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportType exportType,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    final ExportFilePathBuilder s3File = ExportFilePath.getV1Builder(ExportObjectV1.RELATION_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteRelationJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileRelationJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileRelationJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.RELATION, exportType);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  public Step exportRelationJsonStep(ItemReader<RelationVersion> itemReader) {
    String stepName = "exportRelationJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<RelationVersion, ReadRelationVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(relationVersionJsonProcessor())
        .writer(relationJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public RelationVersionJsonProcessor relationVersionJsonProcessor() {
    return new RelationVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadRelationVersionModel> relationJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonRelationVersionWriter.getWriter(ExportObject.RELATION, exportType);
  }

}
