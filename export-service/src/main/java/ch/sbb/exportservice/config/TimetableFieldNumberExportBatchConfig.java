package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TTFN_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TTFN_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.exportservice.entity.lidi.TimetableFieldNumber;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.processor.TimetableFieldNumberCsvProcessor;
import ch.sbb.exportservice.processor.TimetableFieldNumberJsonProcessor;
import ch.sbb.exportservice.reader.TimetableFieldNumberRowMapper;
import ch.sbb.exportservice.reader.TimetableFieldNumberSqlQueryUtil;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvTimetableFieldNumberWriter;
import ch.sbb.exportservice.writer.JsonTimetableFieldNumberWriter;
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
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new TimetableFieldNumberRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportTimetableFieldNumberCsvStep(ItemReader<TimetableFieldNumber> itemReader) {
    final String stepName = "exportTimetableFieldNumberCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<TimetableFieldNumber, TimetableFieldNumberCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(timetableFieldNumberCsvProcessor())
        .writer(timetableFieldNumberCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
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

  @Bean
  @Qualifier(EXPORT_TTFN_CSV_JOB_NAME)
  public Job exportTTFNCsvJob(ItemReader<TimetableFieldNumber> itemReader) {
    return new JobBuilder(EXPORT_TTFN_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTimetableFieldNumberCsvStep(itemReader))
        .next(uploadTimetableFieldNumberCsvFileStep())
        //        .next(deleteTimetableFieldNumberCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadTimetableFieldNumberCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadTimetableFieldNumberCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadTimetableFieldNumberCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }

  /*@Bean
  public Step deleteTimetableFieldNumberCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(deleteTimetableFieldNumberCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteTimetableFieldNumberCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER,
        exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }*/

  @Bean
  @Qualifier(EXPORT_TTFN_JSON_JOB_NAME)
  public Job exportTTFNJsonJob(ItemReader<TimetableFieldNumber> itemReader) {
    return new JobBuilder(EXPORT_TTFN_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTimetableFieldNumberJsonStep(itemReader))
        .next(uploadTimetableFieldNumberJsonFileStep())
        //        .next(deleteTimetableFieldNumberJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadTimetableFieldNumberJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadTimetableFieldNumberJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadTimetableFieldNumberJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }

  /*
    @Bean
    public Step deleteTimetableFieldNumberJsonFileStep() {
      return new StepBuilder("deleteJsonFiles", jobRepository)
          .tasklet(deleteTimetableFieldNumberJsonFileTasklet(null), transactionManager)
          .listener(stepTracerListener)
          .build();
    }

    @Bean
    @StepScope
    public DeleteJsonFileTasklet deleteTimetableFieldNumberJsonFileTasklet(
        @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
      final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TIMETABLE_FIELD_NUMBER,
          exportTypeV2);
      return new DeleteJsonFileTasklet(filePathBuilder);
    }
  */
  @Bean
  public Step exportTimetableFieldNumberJsonStep(ItemReader<TimetableFieldNumber> itemReader) {
    final String stepName = "exportTimetableFieldNumberJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<TimetableFieldNumber, TimetableFieldNumberModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(timetableFieldNumberJsonProcessor())
        .writer(timetableFieldNumberJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public TimetableFieldNumberJsonProcessor timetableFieldNumberJsonProcessor() {
    return new TimetableFieldNumberJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<TimetableFieldNumberModel> timetableFieldNumberJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonWriter.getWriter(ExportObjectV2.TIMETABLE_FIELD_NUMBER, exportTypeV2);
  }

}
