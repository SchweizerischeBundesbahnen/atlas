package ch.sbb.exportservice.job.lidi.ttfn.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TTFN_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TTFN_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.exportservice.job.lidi.ttfn.entity.TimetableFieldNumber;
import ch.sbb.exportservice.job.lidi.ttfn.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.job.lidi.ttfn.processor.TimetableFieldNumberCsvProcessor;
import ch.sbb.exportservice.job.lidi.ttfn.processor.TimetableFieldNumberJsonProcessor;
import ch.sbb.exportservice.job.lidi.ttfn.sql.TimetableFieldNumberRowMapper;
import ch.sbb.exportservice.job.lidi.ttfn.sql.TimetableFieldNumberSqlQueryUtil;
import ch.sbb.exportservice.job.lidi.ttfn.writer.CsvTimetableFieldNumberWriter;
import ch.sbb.exportservice.job.lidi.ttfn.writer.JsonTimetableFieldNumberWriter;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.util.StepUtil;
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
public class TimetableFieldNumberExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvTimetableFieldNumberWriter csvWriter;
  private final JsonTimetableFieldNumberWriter jsonWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<TimetableFieldNumber> timetableFieldNumberReader(
      @Autowired @Qualifier("lineDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<TimetableFieldNumber> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(TimetableFieldNumberSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new TimetableFieldNumberRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_TTFN_CSV_JOB_NAME)
  public Job exportTtfnCsvJob(ItemReader<TimetableFieldNumber> itemReader) {
    return new JobBuilder(EXPORT_TTFN_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTtfnCsvStep(itemReader))
        .next(uploadTtfnCsvFileStep())
        .next(deleteTtfnCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportTtfnCsvStep(ItemReader<TimetableFieldNumber> itemReader) {
    final String stepName = "exportTimetableFieldNumberCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<TimetableFieldNumber, TimetableFieldNumberCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(timetableFieldNumberCsvProcessor())
        .writer(timetableFieldNumberCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public TimetableFieldNumberCsvProcessor timetableFieldNumberCsvProcessor() {
    return new TimetableFieldNumberCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<TimetableFieldNumberCsvModel> timetableFieldNumberCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvWriter.csvWriter(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2);
  }

  // BEGIN: Upload Csv
  @Bean
  public Step uploadTtfnCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadTtfnCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadTtfnCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv

  // BEGIN: Delete Csv
  @Bean
  public Step deleteTtfnCsvFileStep() {
    return new StepBuilder("deleteCsvFile", jobRepository)
        .tasklet(deleteTtfnCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTtfnCsvFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_TTFN_JSON_JOB_NAME)
  public Job exportTtfnJsonJob(ItemReader<TimetableFieldNumber> itemReader) {
    return new JobBuilder(EXPORT_TTFN_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTtfnJsonStep(itemReader))
        .next(uploadTtfnJsonFileStep())
        .next(deleteTtfnJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportTtfnJsonStep(ItemReader<TimetableFieldNumber> itemReader) {
    final String stepName = "exportTimetableFieldNumberJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<TimetableFieldNumber, TimetableFieldNumberVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(timetableFieldNumberJsonProcessor())
        .writer(timetableFieldNumberJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public TimetableFieldNumberJsonProcessor timetableFieldNumberJsonProcessor() {
    return new TimetableFieldNumberJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<TimetableFieldNumberVersionModel> timetableFieldNumberJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonWriter.getWriter(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2);
  }

  // BEGIN: Upload Json
  @Bean
  public Step uploadTtfnJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadTtfnJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadTtfnJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }
  // END: Upload Json

  // BEGIN: Delete Json
  @Bean
  public Step deleteTtfnJsonFileStep() {
    return new StepBuilder("deleteJsonFile", jobRepository)
        .tasklet(deleteTtfnJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTtfnJsonFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json

}
