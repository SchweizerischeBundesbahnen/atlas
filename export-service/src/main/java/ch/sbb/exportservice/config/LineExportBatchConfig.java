package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LINE_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LINE_JSON_JOB_NAME;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.exportservice.entity.lidi.Line;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportFilePath.ExportFilePathBuilder;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.LineCsvModel;
import ch.sbb.exportservice.processor.LineCsvProcessor;
import ch.sbb.exportservice.processor.LineJsonProcessor;
import ch.sbb.exportservice.reader.LineRowMapper;
import ch.sbb.exportservice.reader.LineSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvLineWriter;
import ch.sbb.exportservice.writer.JsonLineWriter;
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
public class LineExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvLineWriter csvWriter;
  private final JsonLineWriter jsonWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<Line> lineReader(
      @Autowired @Qualifier("lineDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<Line> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(LineSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new LineRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportLineCsvStep(ItemReader<Line> itemReader) {
    final String stepName = "exportLineCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<Line, LineCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(lineCsvProcessor())
        .writer(lineCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LineCsvProcessor lineCsvProcessor() {
    return new LineCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<LineCsvModel> lineCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvWriter.csvWriter(ExportObject.LINE, exportType);
  }

  @Bean
  @Qualifier(EXPORT_LINE_CSV_JOB_NAME)
  public Job exportLineCsvJob(ItemReader<Line> itemReader) {
    return new JobBuilder(EXPORT_LINE_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLineCsvStep(itemReader))
        .next(uploadLineCsvFileStep())
        .next(deleteLineCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadLineCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadLineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadLineCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.LINE, exportType);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  public Step deleteLineCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(deleteLineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteLineCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.LINE, exportType);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }

  @Bean
  @Qualifier(EXPORT_LINE_JSON_JOB_NAME)
  public Job exportLineJsonJob(ItemReader<Line> itemReader) {
    return new JobBuilder(EXPORT_LINE_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportLineJsonStep(itemReader))
        .next(uploadLineJsonFileStep())
        .next(deleteLineJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadLineJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadLineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadLineJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.LINE, exportType);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  public Step deleteLineJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(deleteLineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteLineJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePath.getV2Builder(ExportObject.LINE, exportType);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }

  @Bean
  public Step exportLineJsonStep(ItemReader<Line> itemReader) {
    final String stepName = "exportLineJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<Line, LineVersionModelV2>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(lineJsonProcessor())
        .writer(lineJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public LineJsonProcessor lineJsonProcessor() {
    return new LineJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<LineVersionModelV2> lineJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonWriter.getWriter(ExportObject.LINE, exportType);
  }

}
