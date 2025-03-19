package ch.sbb.exportservice.job.lidi.subline.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SUBLINE_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SUBLINE_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.exportservice.job.lidi.subline.entity.Subline;
import ch.sbb.exportservice.job.lidi.subline.model.SublineCsvModel;
import ch.sbb.exportservice.job.lidi.subline.processor.MainlineEnrichingProcessor;
import ch.sbb.exportservice.job.lidi.subline.processor.SublineCsvProcessor;
import ch.sbb.exportservice.job.lidi.subline.processor.SublineJsonProcessor;
import ch.sbb.exportservice.job.lidi.subline.sql.SublineRowMapper;
import ch.sbb.exportservice.job.lidi.subline.sql.SublineSqlQueryUtil;
import ch.sbb.exportservice.job.lidi.subline.writer.CsvSublineWriter;
import ch.sbb.exportservice.job.lidi.subline.writer.JsonSublineWriter;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
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

  private final FileService fileService;
  private final MainlineEnrichingProcessor mainlineEnrichingProcessor;

  @Bean
  @StepScope
  public JdbcCursorItemReader<Subline> sublineReader(
      @Autowired @Qualifier("lineDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<Subline> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SublineSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new SublineRowMapper());
    return itemReader;
  }

  // --- CSV ---
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
  public Step exportSublineCsvStep(ItemReader<Subline> itemReader) {
    final String stepName = "exportSublineCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<Subline, SublineCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(new CompositeItemProcessor<>(sublineMainlineEnrichingProcessor(), sublineCsvProcessor()))
        .writer(sublineCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SublineCsvProcessor sublineCsvProcessor() {
    return new SublineCsvProcessor();
  }

  @Bean
  public ItemProcessor<Subline, Subline> sublineMainlineEnrichingProcessor() {
    return mainlineEnrichingProcessor::addMainlinePropertiesToSubline;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<SublineCsvModel> sublineCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvWriter.csvWriter(ExportObjectV2.SUBLINE, exportTypeV2);
  }

  // BEGIN: Upload Csv
  @Bean
  public Step uploadSublineCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadSublineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadSublineCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.SUBLINE, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv

  // BEGIN: Delete Csv
  @Bean
  public Step deleteSublineCsvFileStep() {
    return new StepBuilder("deleteCsvFile", jobRepository)
        .tasklet(deleteSublineCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSublineCsvFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv

  // --- JSON ---
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
  public Step exportSublineJsonStep(ItemReader<Subline> itemReader) {
    final String stepName = "exportSublineJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<Subline, ReadSublineVersionModelV2>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(new CompositeItemProcessor<>(sublineMainlineEnrichingProcessor(), sublineJsonProcessor()))
        .writer(sublineJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonWriter.getWriter(ExportObjectV2.SUBLINE, exportTypeV2);
  }

  // BEGIN: Upload Json
  @Bean
  public Step uploadSublineJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadSublineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadSublineJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.SUBLINE, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }
  // END: Upload Json

  // BEGIN: Delete Json
  @Bean
  public Step deleteSublineJsonFileStep() {
    return new StepBuilder("deleteJsonFile", jobRepository)
        .tasklet(deleteSublineJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSublineJsonFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json

}
