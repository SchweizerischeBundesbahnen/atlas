package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.export.enumeration.BoDiBatchExportFileName;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.exportservice.entity.bodi.BusinessOrganisation;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.BusinessOrganisationCsvModel;
import ch.sbb.exportservice.processor.BusinessOrganisationCsvProcessor;
import ch.sbb.exportservice.processor.BusinessOrganisationJsonProcessor;
import ch.sbb.exportservice.reader.BusinessOrganisationRowMapper;
import ch.sbb.exportservice.reader.BusinessOrganisationSqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvBusinessOrganisationWriter;
import ch.sbb.exportservice.writer.JsonBusinessOrganisationWriter;
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
public class BusinessOrganisationExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvBusinessOrganisationWriter csvWriter;
  private final JsonBusinessOrganisationWriter jsonWriter;

  @Bean
  @StepScope
  public JdbcCursorItemReader<BusinessOrganisation> businessOrganisationReader(
      @Autowired @Qualifier("businessOrganisationDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    JdbcCursorItemReader<BusinessOrganisation> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(BusinessOrganisationSqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new BusinessOrganisationRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportBusinessOrganisationCsvStep(ItemReader<BusinessOrganisation> itemReader) {
    final String stepName = "exportBusinessOrganisationCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<BusinessOrganisation, BusinessOrganisationCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(businessOrganisationCsvProcessor())
        .writer(BusinessOrganisationCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public BusinessOrganisationCsvProcessor businessOrganisationCsvProcessor() {
    return new BusinessOrganisationCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<BusinessOrganisationCsvModel> BusinessOrganisationCsvWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return csvWriter.csvWriter(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME)
  public Job exportCsvJob(ItemReader<BusinessOrganisation> itemReader) {
    return new JobBuilder(EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportBusinessOrganisationCsvStep(itemReader))
        .next(uploadBusinessOrganisationCsvFileStep())
        .next(deleteBusinessOrganisationCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadBusinessOrganisationCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadBusinessOrganisationCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadBusinessOrganisationCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return new UploadCsvFileTasklet(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  @Bean
  public Step deleteBusinessOrganisationCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(deleteBusinessOrganisationCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet deleteBusinessOrganisationCsvFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType
  ) {
    return new DeleteCsvFileTasklet(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  @Bean
  @Qualifier(EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME)
  public Job exportJsonJob(ItemReader<BusinessOrganisation> itemReader) {
    return new JobBuilder(EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportBusinessOrganisationJsonStep(itemReader))
        .next(uploadBusinessOrganisationJsonFileStep())
        .next(deleteBusinessOrganisationJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step uploadBusinessOrganisationJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadBusinessOrganisationJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadBusinessOrganisationJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new UploadJsonFileTasklet(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  @Bean
  public Step deleteBusinessOrganisationJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(deleteBusinessOrganisationJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet deleteBusinessOrganisationJsonFileTasklet(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return new DeleteJsonFileTasklet(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  @Bean
  public Step exportBusinessOrganisationJsonStep(ItemReader<BusinessOrganisation> itemReader) {
    final String stepName = "exportBusinessOrganisationJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<BusinessOrganisation, BusinessOrganisationVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(businessOrganisationJsonProcessor())
        .writer(businessOrganisationJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public BusinessOrganisationJsonProcessor businessOrganisationJsonProcessor() {
    return new BusinessOrganisationJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<BusinessOrganisationVersionModel> businessOrganisationJsonFileItemWriter(
      @Value("#{jobParameters[exportType]}") ExportType exportType) {
    return jsonWriter.getWriter(exportType, BoDiBatchExportFileName.BUSINESS_ORGANISATION_VERSION);
  }

  // todo: make generic 4 endpoints for file streaming with 2 simple enums and check that exportfilepaths works for current and
  //  V2 structure

}
