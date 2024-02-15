package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.model.PrmBatchExportFileName.PLATFORM_VERSION;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_JSON_JOB_NAME;

import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.exportservice.entity.PlatformVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.processor.PlatformVersionCsvProcessor;
import ch.sbb.exportservice.processor.PlatformVersionJsonProcessor;
import ch.sbb.exportservice.reader.PlatformVersionRowMapper;
import ch.sbb.exportservice.reader.PlatformVersionSqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileCsvDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileJsonDeletingTasklet;
import ch.sbb.exportservice.tasklet.UploadCsvFileTasklet;
import ch.sbb.exportservice.tasklet.UploadJsonFileTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import ch.sbb.exportservice.writer.CsvPlatformVersionWriter;
import ch.sbb.exportservice.writer.JsonPlatformVersionWriter;
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
public class PlatformVersionExportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;
    private final StepTracerListener stepTracerListener;
    private final CsvPlatformVersionWriter csvPlatformVersionWriter;
    private final JsonPlatformVersionWriter jsonPlatformVersionWriter;

    @Bean
    @StepScope
    public JdbcCursorItemReader<PlatformVersion> platformReader(
        @Autowired @Qualifier("prmDataSource") DataSource dataSource,
        @Value("#{jobParameters[exportType]}") PrmExportType prmExportType
    ) {
        JdbcCursorItemReader<PlatformVersion> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql(PlatformVersionSqlQueryUtil.getSqlQuery(prmExportType));
        itemReader.setFetchSize(StepUtils.FETCH_SIZE);
        itemReader.setRowMapper(new PlatformVersionRowMapper());
        return itemReader;
    }

    @Bean
    public Step exportPlatformCsvStep(ItemReader<PlatformVersion> itemReader) {
        final String stepName = "exportPlatformCsvStep";
        return new StepBuilder(stepName, jobRepository)
            .<PlatformVersion, PlatformVersionCsvModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
            .reader(itemReader)
            .processor(platformVersionCsvProcessor())
            .writer(platformCsvWriter(null))
            .faultTolerant()
            .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
            .retryPolicy(StepUtils.getRetryPolicy(stepName))
            .listener(stepTracerListener)
            .build();
    }

    @Bean
    public PlatformVersionCsvProcessor platformVersionCsvProcessor() {
        return new PlatformVersionCsvProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<PlatformVersionCsvModel> platformCsvWriter(
        @Value("#{jobParameters[exportType]}") PrmExportType exportType
    ) {
        return csvPlatformVersionWriter.csvWriter(exportType, PLATFORM_VERSION);
    }

    @Bean
    @Qualifier(EXPORT_PLATFORM_CSV_JOB_NAME)
    public Job exportPlatformCsvJob(ItemReader<PlatformVersion> itemReader) {
        return new JobBuilder(EXPORT_PLATFORM_CSV_JOB_NAME, jobRepository)
            .listener(jobCompletionListener)
            .incrementer(new RunIdIncrementer())
            .flow(exportPlatformCsvStep(itemReader))
            .next(uploadPlatformCsvFileStep())
            .next(deletePlatformCsvFileStep())
            .end()
            .build();
    }

    @Bean
    @Qualifier(EXPORT_PLATFORM_JSON_JOB_NAME)
    public Job exportPlatformJsonJob(ItemReader<PlatformVersion> itemReader) {
        return new JobBuilder(EXPORT_PLATFORM_JSON_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(exportPlatformJsonStep(itemReader))
                .next(uploadPlatformJsonFileStep())
                .next(deletePlatformJsonFileStep())
                .end()
                .build();
    }
    @Bean
    public Step exportPlatformJsonStep(ItemReader<PlatformVersion> itemReader) {
        String stepName = "exportPlatformJsonStep";
        return new StepBuilder(stepName, jobRepository)
            .<PlatformVersion, ReadPlatformVersionModel>chunk(StepUtils.CHUNK_SIZE, transactionManager)
            .reader(itemReader)
            .processor(platformVersionJsonProcessor())
            .writer(platformJsonFileItemWriter(null))
            .faultTolerant()
            .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
            .retryPolicy(StepUtils.getRetryPolicy(stepName))
            .listener(stepTracerListener)
            .build();
    }

    @Bean
    public PlatformVersionJsonProcessor platformVersionJsonProcessor() {
        return new PlatformVersionJsonProcessor();
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<ReadPlatformVersionModel> platformJsonFileItemWriter(
        @Value("#{jobParameters[exportType]}") PrmExportType exportType) {
        return jsonPlatformVersionWriter.getWriter(exportType, PLATFORM_VERSION);
    }

    @Bean
    public Step uploadPlatformCsvFileStep() {
        return new StepBuilder("uploadCsvFile", jobRepository)
            .tasklet(uploadPlatformCsvFileTasklet(null), transactionManager)
            .listener(stepTracerListener)
            .build();
    }

    @Bean
    public Step uploadPlatformJsonFileStep() {
        return new StepBuilder("uploadJsonFile", jobRepository)
                .tasklet(uploadPlatformJsonFileTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    public Step deletePlatformCsvFileStep() {
        return new StepBuilder("deleteCsvFiles", jobRepository)
                .tasklet(filePlatformCsvDeletingTasklet(null), transactionManager)
                .listener(stepTracerListener)
                .build();
    }

    @Bean
    public Step deletePlatformJsonFileStep() {
        return new StepBuilder("deleteJsonFiles", jobRepository)
            .tasklet(filePlatformJsonDeletingTasklet(null), transactionManager)
            .listener(stepTracerListener)
            .build();
    }

    @Bean
    @StepScope
    public UploadCsvFileTasklet uploadPlatformCsvFileTasklet(@Value("#{jobParameters[exportType]}") PrmExportType prmExportType) {
        return new UploadCsvFileTasklet(prmExportType, PLATFORM_VERSION);
    }

    @Bean
    @StepScope
    public UploadJsonFileTasklet uploadPlatformJsonFileTasklet(@Value("#{jobParameters[exportType]}") PrmExportType prmExportType) {
        return new UploadJsonFileTasklet(prmExportType, PLATFORM_VERSION);
    }

    @Bean
    @StepScope
    public FileCsvDeletingTasklet filePlatformCsvDeletingTasklet(@Value("#{jobParameters[exportType]}") PrmExportType prmExportType) {
        return new FileCsvDeletingTasklet(prmExportType, PLATFORM_VERSION);
    }

    @Bean
    @StepScope
    public FileJsonDeletingTasklet filePlatformJsonDeletingTasklet(
        @Value("#{jobParameters[exportType]}") PrmExportType prmExportType) {
        return new FileJsonDeletingTasklet(prmExportType, PLATFORM_VERSION);
    }
}
