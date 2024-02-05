package ch.sbb.exportservice.config;

import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.exportservice.entity.ContactPointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.ContactPointVersionCsvProcessor;
import ch.sbb.exportservice.processor.ContactPointVersionJsonProcessor;
import ch.sbb.exportservice.reader.ContactPointVersionRowMapper;
import ch.sbb.exportservice.reader.ContactPointVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvContactPointVersionWriter;
import ch.sbb.exportservice.writer.JsonContactPointVersionWriter;
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

import javax.sql.DataSource;

import static ch.sbb.exportservice.model.PrmBatchExportFileName.CONTACT_POINT_VERSION;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_JSON_JOB_NAME;

@Configuration
@RequiredArgsConstructor
public class ContactPointVersionExportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;
    private final StepTracerListener stepTracerListener;
    private final CsvContactPointVersionWriter csvContactPointVersionWriter;
    private final JsonContactPointVersionWriter jsonContactPointVersionWriter;

    @Bean
    @StepScope
    public JdbcCursorItemReader<ContactPointVersion> contactPointReader(
            @Autowired @Qualifier("prmDataSource") DataSource dataSource,
            @Value("#{jobParameters[exportType]}") PrmExportType exportType
    ) {
        JdbcCursorItemReader<ContactPointVersion> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql(ContactPointVersionSqlQueryUtil.getSqlQuery(exportType));
        itemReader.setFetchSize(StepUtils.FETCH_SIZE);
        itemReader.setRowMapper(new ContactPointVersionRowMapper());
        return itemReader;
    }

    @Bean
    public Step exportContactPointCsvStep(ItemReader<ContactPointVersion> itemReader) {
        final String stepName = "exportContactPointCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<ContactPointVersion, ContactPointVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
                .reader(itemReader)
                .processor(contactPointVersionCsvProcessor())
                .writer(contactPointCsvWriter(null))
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    public ContactPointVersionCsvProcessor contactPointVersionCsvProcessor() {
        return new ContactPointVersionCsvProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ContactPointVersionCsvModel> contactPointCsvWriter(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType
    ) {
        return csvContactPointVersionWriter.csvWriter(exportType, CONTACT_POINT_VERSION);
    }

    @Bean
    @Qualifier(EXPORT_CONTACT_POINT_CSV_JOB_NAME)
    public Job exportContactPointCsvJob(ItemReader<ContactPointVersion> itemReader) {
        return new JobBuilder(EXPORT_CONTACT_POINT_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(exportContactPointCsvStep(itemReader))
                .next(uploadContactPointCsvFileStep())
                .next(deleteContactPointCsvFileStep())
                .end()
                .build();
    }

    @Bean
    public Step uploadContactPointCsvFileStep() {
        return new StepBuilder("uploadCsvFile", jobRepository)
                .tasklet(uploadContactPointCsvFileTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    @StepScope
    public UploadCsvFileTasklet uploadContactPointCsvFileTasklet(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType
    ) {
        return new UploadCsvFileTasklet(exportType, CONTACT_POINT_VERSION);
    }

    @Bean
    public Step deleteContactPointCsvFileStep() {
        return new StepBuilder("deleteCsvFiles", jobRepository)
                .tasklet(contactPointCsvFileDeletingTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    @StepScope
    public FileCsvDeletingTasklet contactPointCsvFileDeletingTasklet(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType
    ) {
        return new FileCsvDeletingTasklet(exportType, CONTACT_POINT_VERSION);
    }

    @Bean
    @Qualifier(EXPORT_CONTACT_POINT_JSON_JOB_NAME)
    public Job exportContactPointJsonJob(ItemReader<ContactPointVersion> itemReader) {
        return new JobBuilder(EXPORT_CONTACT_POINT_JSON_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(exportContactPointJsonStep(itemReader))
                .next(uploadContactPointJsonFileStep())
                .next(deleteContactPointJsonFileStep())
                .end()
                .build();
    }

    @Bean
    public Step uploadContactPointJsonFileStep() {
        return new StepBuilder("uploadJsonFile", jobRepository)
                .tasklet(uploadContactPointJsonFileTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    @StepScope
    public UploadJsonFileTasklet uploadContactPointJsonFileTasklet(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
        return new UploadJsonFileTasklet(exportType, CONTACT_POINT_VERSION);
    }

    @Bean
    public Step deleteContactPointJsonFileStep() {
        return new StepBuilder("deleteJsonFiles", jobRepository)
                .tasklet(fileContactPointJsonDeletingTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    @StepScope
    public FileJsonDeletingTasklet fileContactPointJsonDeletingTasklet(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
        return new FileJsonDeletingTasklet(exportType, CONTACT_POINT_VERSION);
    }

    @Bean
    public Step exportContactPointJsonStep(ItemReader<ContactPointVersion> itemReader) {
        String stepName = "exportContactPointJsonStep";
        return new StepBuilder(stepName, jobRepository)
                .<ContactPointVersion, ReadContactPointVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
                .reader(itemReader)
                .processor(contactPointVersionJsonProcessor())
                .writer(contactPointJsonFileItemWriter(null))
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    public ContactPointVersionJsonProcessor contactPointVersionJsonProcessor() {
        return new ContactPointVersionJsonProcessor();
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<ReadContactPointVersionModel> contactPointJsonFileItemWriter(
            @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
        return jsonContactPointVersionWriter.getWriter(exportType, CONTACT_POINT_VERSION);
    }

}
