package ch.sbb.exportservice.job.bodi.businessorganisation.batch;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_CSV_JOB_NAME;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_BUSINESS_ORGANISATION_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisation;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisationCsvModel;
import ch.sbb.exportservice.job.bodi.businessorganisation.processor.BusinessOrganisationCsvProcessor;
import ch.sbb.exportservice.job.bodi.businessorganisation.processor.BusinessOrganisationJsonProcessor;
import ch.sbb.exportservice.job.bodi.businessorganisation.sql.BusinessOrganisationRowMapper;
import ch.sbb.exportservice.job.bodi.businessorganisation.sql.BusinessOrganisationSqlQueryUtil;
import ch.sbb.exportservice.job.bodi.businessorganisation.writer.CsvBusinessOrganisationWriter;
import ch.sbb.exportservice.job.bodi.businessorganisation.writer.JsonBusinessOrganisationWriter;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.utile.StepUtil;
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

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<BusinessOrganisation> businessOrganisationReader(
      @Autowired @Qualifier("businessOrganisationDirectoryDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<BusinessOrganisation> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(BusinessOrganisationSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new BusinessOrganisationRowMapper());
    return itemReader;
  }

  // --- CSV ---
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
  public Step exportBusinessOrganisationCsvStep(ItemReader<BusinessOrganisation> itemReader) {
    final String stepName = "exportBusinessOrganisationCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<BusinessOrganisation, BusinessOrganisationCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(businessOrganisationCsvProcessor())
        .writer(BusinessOrganisationCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvWriter.csvWriter(ExportObjectV2.BUSINESS_ORGANISATION, exportTypeV2);
  }

  // BEGIN: Upload Csv
  @Bean
  public Step uploadBusinessOrganisationCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadBusinessOrganisationCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadBusinessOrganisationCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.BUSINESS_ORGANISATION, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv

  // BEGIN: Delete Csv
  @Bean
  public Step deleteBusinessOrganisationCsvFileStep() {
    return new StepBuilder("deleteCsvFile", jobRepository)
        .tasklet(deleteBusinessOrganisationCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteBusinessOrganisationCsvFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv

  // --- JSON ---
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
  public Step exportBusinessOrganisationJsonStep(ItemReader<BusinessOrganisation> itemReader) {
    final String stepName = "exportBusinessOrganisationJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<BusinessOrganisation, BusinessOrganisationVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(businessOrganisationJsonProcessor())
        .writer(businessOrganisationJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
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
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonWriter.getWriter(ExportObjectV2.BUSINESS_ORGANISATION, exportTypeV2);
  }

  // BEGIN: Upload Json
  @Bean
  public Step uploadBusinessOrganisationJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadBusinessOrganisationJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadBusinessOrganisationJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.BUSINESS_ORGANISATION, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }
  // END: Upload Json

  // BEGIN: Delete Json
  @Bean
  public Step deleteBusinessOrganisationJsonFileStep() {
    return new StepBuilder("deleteJsonFile", jobRepository)
        .tasklet(deleteBusinessOrganisationJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteBusinessOrganisationJsonFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json

}
