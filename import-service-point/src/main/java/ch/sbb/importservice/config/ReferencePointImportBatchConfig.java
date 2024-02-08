package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_REFERENCE_POINT_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.ReferencePointCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.ReferencePointApiWriter;
import java.io.File;
import java.util.Collections;
import java.util.List;
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

@Configuration
@Slf4j
public class ReferencePointImportBatchConfig extends BaseImportBatchJob {

    public static final int PRM_CHUNK_SIZE = 20;
    private final ReferencePointApiWriter referencePointApiWriter;
    private final ReferencePointCsvService referencePointCsvService;

    protected ReferencePointImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                              JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
                                              ReferencePointApiWriter referencePointApiWriter, ReferencePointCsvService referencePointCsvService) {
        super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
        this.referencePointApiWriter = referencePointApiWriter;
        this.referencePointCsvService = referencePointCsvService;
    }

    @StepScope
    @Bean
    public ThreadSafeListItemReader<ReferencePointCsvModelContainer> referencePointListItemReader(
            @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        List<ReferencePointCsvModel> actualReferencePointCsvModels;
        if (pathToFile != null) {
            File file = new File(pathToFile);
            actualReferencePointCsvModels = referencePointCsvService.getActualCsvModels(file);
        } else {
            actualReferencePointCsvModels = referencePointCsvService.getActualCsvModelsFromS3();
        }
        List<ReferencePointCsvModelContainer> referencePointCsvModelContainers = referencePointCsvService.mapToReferencePointCsvModelContainers(
                actualReferencePointCsvModels);
        long prunedReferencePointModels = referencePointCsvModelContainers.stream()
                .mapToLong(i -> i.getCreateModels().size()).sum();
        log.info("Found " + prunedReferencePointModels + " referencePoints to import...");
        log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
        return new ThreadSafeListItemReader<>(Collections.synchronizedList(referencePointCsvModelContainers));
    }

    @Bean
    public Step parseReferencePointCsvStep(ThreadSafeListItemReader<ReferencePointCsvModelContainer> referencePointListItemReader) {
        String stepName = "parseReferencePointCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<ReferencePointCsvModelContainer, ReferencePointCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
                .reader(referencePointListItemReader)
                .writer(referencePointApiWriter)
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .taskExecutor(asyncTaskExecutor())
                .build();
    }

    @Bean
    public Job importReferencePointCsvJob(ThreadSafeListItemReader<ReferencePointCsvModelContainer> referencePointListItemReader) {
        return new JobBuilder(IMPORT_REFERENCE_POINT_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(parseReferencePointCsvStep(referencePointListItemReader))
                .end()
                .build();
    }

}
