package ch.sbb.importservice.config;

import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.RelationCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.RelationApiWriter;
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

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_RELATION_CSV_JOB_NAME;

@Configuration
@Slf4j
public class RelationImportBatchConfig extends BaseImportBatchJob{

    public static final int PRM_CHUNK_SIZE = 20;
    private final RelationApiWriter relationApiWriter;
    private final RelationCsvService relationCsvService;

    protected RelationImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                              JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
                                              RelationApiWriter relationApiWriter, RelationCsvService relationCsvService) {
        super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
        this.relationApiWriter = relationApiWriter;
        this.relationCsvService = relationCsvService;
    }

    @StepScope
    @Bean
    public ThreadSafeListItemReader<RelationCsvModelContainer> relationListItemReader(
            @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        List<RelationCsvModel> actualRelationCsvModels;
        if (pathToFile != null) {
            File file = new File(pathToFile);
            actualRelationCsvModels = relationCsvService.getActualCsvModels(file);
        } else {
            actualRelationCsvModels = relationCsvService.getActualCsvModelsFromS3();
        }
        List<RelationCsvModelContainer> relationCsvModelContainers = relationCsvService.mapToRelationCsvModelContainers(
                actualRelationCsvModels);
        long prunedRelationModels = relationCsvModelContainers.stream()
                .mapToLong(i -> i.getCreateModels().size()).sum();
        log.info("Found " + prunedRelationModels + " relations to import...");
        log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
        return new ThreadSafeListItemReader<>(Collections.synchronizedList(relationCsvModelContainers));
    }

    @Bean
    public Step parseRelationCsvStep(ThreadSafeListItemReader<RelationCsvModelContainer> relationListItemReader) {
        String stepName = "parseRelationCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<RelationCsvModelContainer, RelationCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
                .reader(relationListItemReader)
                .writer(relationApiWriter)
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .taskExecutor(asyncTaskExecutor())
                .build();
    }

    @Bean
    public Job importRelationCsvJob(ThreadSafeListItemReader<RelationCsvModelContainer> relationListItemReader) {
        return new JobBuilder(IMPORT_RELATION_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(parseRelationCsvStep(relationListItemReader))
                .end()
                .build();
    }

}
