package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.exportservice.entity.prm.ToiletVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.processor.ToiletVersionCsvProcessor;
import ch.sbb.exportservice.processor.ToiletVersionJsonProcessor;
import ch.sbb.exportservice.reader.ToiletVersionRowMapper;
import ch.sbb.exportservice.reader.ToiletVersionSqlQueryUtil;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvToiletVersionWriter;
import ch.sbb.exportservice.writer.JsonToiletVersionWriter;
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
public class ToiletVersionExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvToiletVersionWriter csvToiletVersionWriter;
  private final JsonToiletVersionWriter jsonToiletVersionWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ToiletVersion> toiletReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<ToiletVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(ToiletVersionSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new ToiletVersionRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportToiletCsvStep(ItemReader<ToiletVersion> itemReader) {
    final String stepName = "exportToiletCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ToiletVersion, ToiletVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(toiletVersionCsvProcessor())
        .writer(toiletCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ToiletVersionCsvProcessor toiletVersionCsvProcessor() {
    return new ToiletVersionCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ToiletVersionCsvModel> toiletCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvToiletVersionWriter.csvWriter(ExportObjectV2.TOILET, exportTypeV2);
  }

  @Bean
  @Qualifier(EXPORT_TOILET_CSV_JOB_NAME)
  public Job toiletPointCsvJob(ItemReader<ToiletVersion> itemReader) {
    return new JobBuilder(EXPORT_TOILET_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportToiletCsvStep(itemReader))
        //        .next(uploadToiletCsvFileStep())
        //        .next(deleteToiletCsvFileStep())
        .end()
        .build();
  }

  /*@Bean
  public Step uploadToiletCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadToiletCsvFileTasklet(null), transactionManager)
        .tasklet(uploadToiletCsvFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadToiletCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    return new UploadCsvFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadToiletCsvFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.TOILET_VERSION, exportTypeV1);
    return new UploadCsvFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteToiletCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(toiletCsvFileDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet toiletCsvFileDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    return new DeleteCsvFileTasklet(filePathBuilder);
  }*/

  @Bean
  @Qualifier(EXPORT_TOILET_JSON_JOB_NAME)
  public Job exportToiletJsonJob(ItemReader<ToiletVersion> itemReader) {
    return new JobBuilder(EXPORT_TOILET_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportToiletJsonStep(itemReader))
        //        .next(uploadToiletJsonFileStep())
        //        .next(deleteToiletJsonFileStep())
        .end()
        .build();
  }

  /*@Bean
  public Step uploadToiletJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadToiletJsonFileTasklet(null), transactionManager)
        .tasklet(uploadToiletJsonFileTaskletV1(null, null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadToiletJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    return new UploadJsonFileTasklet(filePathBuilder, filePathBuilder);
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadToiletJsonFileTaskletV1(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2,
      @Value("#{jobParameters[exportTypeV1]}") ExportTypeV1 exportTypeV1
  ) {
    final ExportFilePathBuilder systemFile = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    final ExportFilePathBuilder s3File = ExportFilePathV1.getV1Builder(ExportObjectV1.TOILET_VERSION, exportTypeV1);
    return new UploadJsonFileTasklet(systemFile, s3File);
  }

  @Bean
  public Step deleteToiletJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileToiletJsonDeletingTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileToiletJsonDeletingTasklet(
      @Value("#{jobParameters[exportType]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathBuilder filePathBuilder = ExportFilePathV1.getV2Builder(ExportObjectV2.TOILET, exportTypeV2);
    return new DeleteJsonFileTasklet(filePathBuilder);
  }*/

  @Bean
  public Step exportToiletJsonStep(ItemReader<ToiletVersion> itemReader) {
    String stepName = "exportToiletPointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ToiletVersion, ReadToiletVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(toiletVersionJsonProcessor())
        .writer(toiletJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public ToiletVersionJsonProcessor toiletVersionJsonProcessor() {
    return new ToiletVersionJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<ReadToiletVersionModel> toiletJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonToiletVersionWriter.getWriter(ExportObjectV2.TOILET, exportTypeV2);
  }

}
