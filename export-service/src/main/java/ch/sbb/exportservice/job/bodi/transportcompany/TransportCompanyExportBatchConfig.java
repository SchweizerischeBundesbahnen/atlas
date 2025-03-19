package ch.sbb.exportservice.job.bodi.transportcompany;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
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
public class TransportCompanyExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvTransportCompanyWriter csvTransportCompanyWriter;
  private final JsonTransportCompanyWriter jsonTransportCompanyWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<TransportCompany> transportCompanyReader(
      @Autowired @Qualifier("businessOrganisationDirectoryDataSource") DataSource dataSource
  ) {
    JdbcCursorItemReader<TransportCompany> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(TransportCompanySqlQueryUtil.getSqlQuery());
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new TransportCompanyRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME)
  public Job exportTransportCompanyCsvJob(ItemReader<TransportCompany> itemReader) {
    return new JobBuilder(EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTransportCompanyCsvStep(itemReader))
        .next(uploadTransportCompanyCsvFileStep())
        .next(deleteTransportCompanyCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportTransportCompanyCsvStep(ItemReader<TransportCompany> itemReader) {
    final String stepName = "exportTransportCompanyCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<TransportCompany, TransportCompanyCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(transportCompanyCsvProcessor())
        .writer(transportCompanyCsvWriter())
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public TransportCompanyCsvProcessor transportCompanyCsvProcessor() {
    return new TransportCompanyCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<TransportCompanyCsvModel> transportCompanyCsvWriter(
  ) {
    return csvTransportCompanyWriter.csvWriter(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.FULL);
  }

  // BEGIN: Upload Csv
  @Bean
  public Step uploadTransportCompanyCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadTransportCompanyCsvFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadTransportCompanyCsvFileTasklet() {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.FULL)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv

  // BEGIN: Delete Csv
  @Bean
  public Step deleteTransportCompanyCsvFileStep() {
    return new StepBuilder("deleteCsvFile", jobRepository)
        .tasklet(deleteTransportCompanyCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTransportCompanyCsvFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv

  // --- JSON ---
  @Bean
  @Qualifier(EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME)
  public Job exportTransportCompanyJsonJob(ItemReader<TransportCompany> itemReader) {
    return new JobBuilder(EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTransportCompanyJsonStep(itemReader))
        .next(uploadTransportCompanyJsonFileStep())
        .next(deleteTransportCompanyJsonFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportTransportCompanyJsonStep(ItemReader<TransportCompany> itemReader) {
    String stepName = "exportTransportCompanyJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<TransportCompany, TransportCompanyModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(transportCompanyJsonProcessor())
        .writer(transportCompanyJsonFileItemWriter())
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public TransportCompanyJsonProcessor transportCompanyJsonProcessor() {
    return new TransportCompanyJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<TransportCompanyModel> transportCompanyJsonFileItemWriter() {
    return jsonTransportCompanyWriter.getWriter(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.FULL);
  }

  // BEGIN: Upload Json
  @Bean
  public Step uploadTransportCompanyJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadTransportCompanyJsonFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadTransportCompanyJsonFileTasklet() {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.FULL)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePath);
  }
  // END: Upload Json

  // BEGIN: Delete Json
  @Bean
  public Step deleteTransportCompanyJsonFileStep() {
    return new StepBuilder("deleteJsonFile", jobRepository)
        .tasklet(deleteTransportCompanyJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteTransportCompanyJsonFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Json

}
