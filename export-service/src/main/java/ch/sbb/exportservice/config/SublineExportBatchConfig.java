package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SUBLINE_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SUBLINE_JSON_JOB_NAME;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.exportservice.entity.lidi.Subline;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportFilePath.ExportFilePathBuilder;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.SublineCsvModel;
import ch.sbb.exportservice.processor.SublineCsvProcessor;
import ch.sbb.exportservice.processor.SublineJsonProcessor;
import ch.sbb.exportservice.reader.SublineRowMapper;
import ch.sbb.exportservice.reader.SublineSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvSublineWriter;
import ch.sbb.exportservice.writer.JsonSublineWriter;
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
public class SublineExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvSublineWriter csvWriter;
  private final JsonSublineWriter jsonWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<Subline> sublineReader(
      @Autowired @Qualifier("lineDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<Subline> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SublineSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new SublineRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportSublineCsvStep(ItemReader<Subline> itemReader) {
    final String stepName = "exportSublineCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<Subline, SublineCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(sublineCsvProcessor())
        .writer(sublineCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SublineCsvProcessor sublineCsvProcessor() {
    return new SublineCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<SublineCsvModel> sublineCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvWriter.csvWriter(ExportObject.SUBLINE, exportType);
  }

  @Bean
  @Qualifier(EXPORT_SUBLINE_CSV_JOB_NAME)
  public Job exportSublineCsvJob(ItemReader<Subline> itemReader) {
    return new JobBuilder(EXPORT_SUBLINE_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSublineCsvStep(itemReader))
        .next(uploadSublineCsvFileStep())
        .next(deleteSublineCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadSublineCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadSublineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadSublineCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.SUBLINE, exportType);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  public Step deleteSublineCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(deleteSublineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteSublineCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.SUBLINE, exportType);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @Qualifier(EXPORT_SUBLINE_JSON_JOB_NAME)
  public Job exportSublineJsonJob(ItemReader<Subline> itemReader) {
    return new JobBuilder(EXPORT_SUBLINE_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSublineJsonStep(itemReader))
        .next(uploadSublineJsonFileStep())
        .next(deleteSublineJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadSublineJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadSublineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadSublineJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.SUBLINE, exportType);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  public Step deleteSublineJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(deleteSublineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteSublineJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.SUBLINE, exportType);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  public Step exportSublineJsonStep(ItemReader<Subline> itemReader) {
    final String stepName = "exportSublineJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<Subline, ReadSublineVersionModelV2>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(sublineJsonProcessor())
        .writer(sublineJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SublineJsonProcessor sublineJsonProcessor() {
    return new SublineJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadSublineVersionModelV2> sublineJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonWriter.getWriter(ExportObject.SUBLINE, exportType);
  }

}
