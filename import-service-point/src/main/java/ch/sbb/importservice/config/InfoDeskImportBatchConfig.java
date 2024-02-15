package ch.sbb.importservice.config;


import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.ContactPointCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.ContactPointApiWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_INFO_DESK_CSV_JOB_NAME;

@Configuration
@Slf4j
public class InfoDeskImportBatchConfig extends BaseImportBatchJob{
    public static final String INFO_DESK_FILENAME = "PRM_INFO_DESKS";
    private static final int PRM_CHUNK_SIZE = 20;
    private final ContactPointApiWriter contactPointApiWriter;
    private final ContactPointCsvService contactPointCsvService;



    protected InfoDeskImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                        JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
                                        ContactPointApiWriter contactPointApiWriter, ContactPointCsvService contactPointCsvService) {
        super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
        this.contactPointApiWriter = contactPointApiWriter;
        this.contactPointCsvService = contactPointCsvService;
    }

    @StepScope
    @Bean
    public ThreadSafeListItemReader<ContactPointCsvModelContainer> infoDeskListItemReader(
            @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        List<ContactPointCsvModel> actualContactPointCsvModels;

        if (pathToFile != null) {
            File file = new File(pathToFile);
            actualContactPointCsvModels = contactPointCsvService.loadFromFile(file, ContactPointType.INFORMATION_DESK);
        }
        else {
            actualContactPointCsvModels = contactPointCsvService.loadFileFromS3(INFO_DESK_FILENAME, IMPORT_INFO_DESK_CSV_JOB_NAME, ContactPointType.INFORMATION_DESK);
        }

        List<ContactPointCsvModelContainer> contactPointCsvModelContainers = contactPointCsvService.mapToContactPointCsvModelContainers(
                actualContactPointCsvModels);
        long prunedContactPointModels = contactPointCsvModelContainers.stream()
                .mapToLong(i -> i.getCreateModels().size()).sum();
        log.info("Found " + prunedContactPointModels + " info desks to import...");
        log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
        return new ThreadSafeListItemReader<>(Collections.synchronizedList(contactPointCsvModelContainers));
    }

    @Bean
    public Step parseInfoDeskCsvStep(ThreadSafeListItemReader<ContactPointCsvModelContainer> infoDeskListItemReader) {
        String stepName = "parseInfoDeskCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<ContactPointCsvModelContainer, ContactPointCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
                .reader(infoDeskListItemReader)
                .writer(contactPointApiWriter)
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .taskExecutor(asyncTaskExecutor())
                .build();
    }

    @Bean
    public Job importInfoDeskCsvJob(ThreadSafeListItemReader<ContactPointCsvModelContainer> infoDeskListItemReader) {
        return new JobBuilder(IMPORT_INFO_DESK_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(parseInfoDeskCsvStep(infoDeskListItemReader))
                .end()
                .build();
    }
}