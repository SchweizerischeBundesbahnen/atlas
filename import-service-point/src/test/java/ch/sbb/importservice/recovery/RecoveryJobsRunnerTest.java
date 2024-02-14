package ch.sbb.importservice.recovery;

import static ch.sbb.importservice.utils.JobDescriptionConstants.EXECUTION_TYPE_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TOILET_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_TRAFFIC_POINT_CSV_JOB_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.DefaultApplicationArguments;

class RecoveryJobsRunnerTest {

  private RecoveryJobsRunner recoveryJobsRunner;

  @Mock
  private JobExplorer jobExplorer;

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private JobInstance jobInstance;

  @Mock
  private JobParameters jobParameters;

  @Mock
  private JobExecution jobExecution;

  @Mock
  private FileService fileService;

  @Mock
  @Qualifier(IMPORT_SERVICE_POINT_CSV_JOB_NAME)
  private Job importServicePointCsvJob;

  @Mock
  @Qualifier(IMPORT_LOADING_POINT_CSV_JOB_NAME)
  private Job importLoadingPointCsvJob;

  @Mock
  @Qualifier(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME)
  private Job importTrafficPointCsvJob;

  @Mock
  @Qualifier(IMPORT_STOP_POINT_CSV_JOB_NAME)
  private Job importStopPointCsvJob;

  @Mock
  @Qualifier(IMPORT_PLATFORM_CSV_JOB_NAME)
  private Job importPlatformCsvJob;

  @Mock
  @Qualifier(IMPORT_REFERENCE_POINT_CSV_JOB_NAME)
  private Job importReferencePointCsvJob;

  @Mock
  @Qualifier(IMPORT_TOILET_CSV_JOB_NAME)
  private Job importToiletPointCsvJob;

  @Mock
  @Qualifier(IMPORT_PARKING_LOT_CSV_JOB_NAME)
  private Job importParkingLotCsvJob;

  @Mock
  private ImportProcessedItemRepository importProcessedItemRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, jobLauncher, jobRepository, importProcessedItemRepository,
        importServicePointCsvJob, importLoadingPointCsvJob, importTrafficPointCsvJob, importStopPointCsvJob,
        importPlatformCsvJob, importReferencePointCsvJob, importToiletPointCsvJob, importParkingLotCsvJob, fileService);
  }

  @Test
  void shouldNotRecoverAnyJob() throws Exception {
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportServicePointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportLoadingPointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_LOADING_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportTrafficPointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_TRAFFIC_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportStopPointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_STOP_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportPlatformCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_PLATFORM_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportReferencePointCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_REFERENCE_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importReferencePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportToiletCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_TOILET_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importToiletPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importParkingLotCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverImportParkingLotCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(IMPORT_PARKING_LOT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(importParkingLotCsvJob), any());
    verify(jobLauncher, never()).run(eq(importServicePointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importLoadingPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importTrafficPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importStopPointCsvJob), any());
    verify(jobLauncher, never()).run(eq(importPlatformCsvJob), any());
    verify(jobLauncher, never()).run(eq(importToiletPointCsvJob), any());
    verify(fileService).clearDir();
  }

}
