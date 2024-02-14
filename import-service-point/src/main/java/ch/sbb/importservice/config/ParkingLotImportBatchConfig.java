package ch.sbb.importservice.config;

import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PARKING_LOT_CSV_JOB_NAME;

import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import ch.sbb.importservice.reader.ThreadSafeListItemReader;
import ch.sbb.importservice.service.csv.ParkingLotCsvService;
import ch.sbb.importservice.utils.StepUtils;
import ch.sbb.importservice.writer.prm.ParkingLotApiWriter;
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
public class ParkingLotImportBatchConfig extends BaseImportBatchJob {

    public static final int PRM_CHUNK_SIZE = 20;
    private final ParkingLotApiWriter parkingLotApiWriter;
    private final ParkingLotCsvService parkingLotCsvService;

    protected ParkingLotImportBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                              JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener,
                                              ParkingLotApiWriter parkingLotApiWriter, ParkingLotCsvService parkingLotCsvService) {
        super(jobRepository, transactionManager, jobCompletionListener, stepTracerListener);
        this.parkingLotApiWriter = parkingLotApiWriter;
        this.parkingLotCsvService = parkingLotCsvService;
    }

    @StepScope
    @Bean
    public ThreadSafeListItemReader<ParkingLotCsvModelContainer> parkingLotListItemReader(
            @Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        List<ParkingLotCsvModel> actualParkingLotCsvModels;
        if (pathToFile != null) {
            File file = new File(pathToFile);
            actualParkingLotCsvModels = parkingLotCsvService.getActualCsvModels(file);
        } else {
            actualParkingLotCsvModels = parkingLotCsvService.getActualCsvModelsFromS3();
        }
        List<ParkingLotCsvModelContainer> parkingLotCsvModelContainers = parkingLotCsvService.mapToParkingLotCsvModelContainers(
                actualParkingLotCsvModels);
        long prunedParkingLotModels = parkingLotCsvModelContainers.stream()
                .mapToLong(i -> i.getCreateModels().size()).sum();
        log.info("Found " + prunedParkingLotModels + " parkingLots to import...");
        log.info("Start sending requests to prm-directory with chunkSize: {}...", PRM_CHUNK_SIZE);
        return new ThreadSafeListItemReader<>(Collections.synchronizedList(parkingLotCsvModelContainers));
    }

    @Bean
    public Step parseParkingLotCsvStep(ThreadSafeListItemReader<ParkingLotCsvModelContainer> parkingLotListItemReader) {
        String stepName = "parseParkingLotCsvStep";
        return new StepBuilder(stepName, jobRepository)
                .<ParkingLotCsvModelContainer, ParkingLotCsvModelContainer>chunk(PRM_CHUNK_SIZE, transactionManager)
                .reader(parkingLotListItemReader)
                .writer(parkingLotApiWriter)
                .faultTolerant()
                .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
                .retryPolicy(StepUtils.getRetryPolicy(stepName))
                .listener(stepTracerListener)
                .taskExecutor(asyncTaskExecutor())
                .build();
    }

    @Bean
    public Job importParkingLotCsvJob(ThreadSafeListItemReader<ParkingLotCsvModelContainer> parkingLotListItemReader) {
        return new JobBuilder(IMPORT_PARKING_LOT_CSV_JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .incrementer(new RunIdIncrementer())
                .flow(parseParkingLotCsvStep(parkingLotListItemReader))
                .end()
                .build();
    }

}
