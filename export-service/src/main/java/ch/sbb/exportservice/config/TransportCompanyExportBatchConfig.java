package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRANSPORT_COMPANY_JSON_JOB_NAME;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.exportservice.entity.bodi.TransportCompany;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.BoDiBatchExportFileName;
import ch.sbb.exportservice.model.BoDiExportType;
import ch.sbb.exportservice.model.TransportCompanyCsvModel;
import ch.sbb.exportservice.processor.TransportCompanyCsvProcessor;
import ch.sbb.exportservice.processor.TransportCompanyJsonProcessor;
import ch.sbb.exportservice.reader.TransportCompanyRowMapper;
import ch.sbb.exportservice.reader.TransportCompanySqlQueryUtil;
import ch.sbb.exportservice.tasklet.DeleteCsvFileTasklet;
import ch.sbb.exportservice.tasklet.DeleteJsonFileTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvTransportCompanyWriter;
import ch.sbb.exportservice.writer.JsonTransportCompanyWriter;
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

  @Bean
  @StepScope
  public JdbcCursorItemReader<TransportCompany> transportCompanyReader(
      @Autowired @Qualifier("businessOrganisationDirectoryDataSource") DataSource dataSource
  ) {
    JdbcCursorItemReader<TransportCompany> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(TransportCompanySqlQueryUtil.getSqlQuery());
    itemReader.setFetchSize(StepUtils.FETCH_SIZE);
    itemReader.setRowMapper(new TransportCompanyRowMapper());
    return itemReader;
  }

  @Bean
  public Step exportTransportCompanyCsvStep(ItemReader<TransportCompany> itemReader) {
    final String stepName = "exportTransportCompanyCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<TransportCompany, TransportCompanyCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(transportCompanyCsvProcessor())
        .writer(transportCompanyCsvWriter())
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
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
    return csvTransportCompanyWriter.csvWriter(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

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
  public Step uploadTransportCompanyCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadTransportCompanyCsvFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTasklet uploadTransportCompanyCsvFileTasklet(
  ) {
    return new UploadCsvFileTasklet(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

  @Bean
  public Step deleteTransportCompanyCsvFileStep() {
    return new StepBuilder("deleteCsvFiles", jobRepository)
        .tasklet(transportCompanyCsvFileDeletingTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteCsvFileTasklet transportCompanyCsvFileDeletingTasklet(
  ) {
    return new DeleteCsvFileTasklet(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

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
  public Step uploadTransportCompanyJsonFileStep() {
    return new StepBuilder("uploadJsonFile", jobRepository)
        .tasklet(uploadTransportCompanyJsonFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTasklet uploadTransportCompanyJsonFileTasklet() {
    return new UploadJsonFileTasklet(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

  @Bean
  public Step deleteTransportCompanyJsonFileStep() {
    return new StepBuilder("deleteJsonFiles", jobRepository)
        .tasklet(fileTransportCompanyJsonDeletingTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public DeleteJsonFileTasklet fileTransportCompanyJsonDeletingTasklet() {
    return new DeleteJsonFileTasklet(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

  @Bean
  public Step exportTransportCompanyJsonStep(ItemReader<TransportCompany> itemReader) {
    String stepName = "exportTransportCompanyJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<TransportCompany, TransportCompanyModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(transportCompanyJsonProcessor())
        .writer(transportCompanyJsonFileItemWriter())
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
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
    return jsonTransportCompanyWriter.getWriter(BoDiExportType.FULL, BoDiBatchExportFileName.TRANSPORT_COMPANY);
  }

}
