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

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TICKET_COUNTER_CSV_JOB_NAME;

@Configuration
@Slf4j
public class TicketCounterImportBatchConfig extends BaseImportBatchJob{

    public static final String TICKET_COUNTER_FILENAME = "PRM_TICKET_COUNTERS";
    private static final int PRM_CHUNK_SIZE = 20;
    private final ContactPointApiWriter contactPointApiWriter;
    private final ContactPointCsvService contactPointCsvService;



    protected TicketCounterImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                             JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
                                             ContactPointApiWriter contactPointApiWriter, ContactPointCsvService contactPointCsvService) {
        super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
        this.contactPointApiWriter = contactPointApiWriter;
        this.contactPointCsvService = contactPointCsvService;
    }

    @StepScope
    @Bean
    public ThreadSafeListItemReader<ContactPointCsvModelContainer> ticketCounterListItemReader(
            @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        List<ContactPointCsvModel> actualContactPointCsvModels;

        if (pathToFile != null) {
            File file = new File(pathToFile);
            actualContactPointCsvModels = contactPointCsvService.loadFromFile(file, ContactPointType.TICKET_COUNTER);
        }
        else {
            actualContactPointCsvModels = contactPointCsvService.loadFileFromS3(TICKET_COUNTER_FILENAME, IMPORT_TICKET_COUNTER_CSV_JOB_NAME, ContactPointType.TICKET_COUNTER);
        }

        List<ContactPointCsvModelContainer> contactPointCsvModelContainers = contactPointCsvService.mapToContactPointCsvModelContainers(
                actualContactPointCsvModels);
        long prunedContactPointModels = contactPointCsvModelContainers.stream()
                .mapToLong(i -> i.getCreateModels().size()).sum();
        log.info("Found " + prunedContactPointModels + " ticket counters to import...");
        log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
        return new ThreadSafeListItemReader<>(Collections.synchronizedList(contactPointCsvModelContainers));
    }

    @Bean
    public Step parseTicketCounterCsvStep(ThreadSafeListItemReader<ContactPointCsvModelContainer> ticketCounterListItemReader) {
        String stepName = "parseTicketCounterCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<ContactPointCsvModelContainer, ContactPointCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
                .reader(ticketCounterListItemReader)
                .writer(contactPointApiWriter)
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .taskExecutor(asyncTaskExecutor())
                .build();
    }

    @Bean
    public Job importTicketCounterCsvJob(ThreadSafeListItemReader<ContactPointCsvModelContainer> ticketCounterListItemReader) {
        return new JobBuilder(IMPORT_TICKET_COUNTER_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(parseTicketCounterCsvStep(ticketCounterListItemReader))
                .end()
                .build();
    }


}
