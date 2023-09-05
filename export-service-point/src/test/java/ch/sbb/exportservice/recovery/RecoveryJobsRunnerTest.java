package ch.sbb.exportservice.recovery;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RecoveryJobsRunnerTest {

  private RecoveryJobsRunner recoveryJobsRunner;

  @Mock
  private JobExplorer jobExplorer;

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private ExportServicePointJobService exportServicePointJobService;

  @Mock
  private ExportTrafficPointElementJobService exportTrafficPointElementJobService;

  @Mock
  private ExportLoadingPointJobService exportLoadingPointJobService;

  @Mock
  private JobInstance jobInstance;

  @Mock
  private JobParameters jobParameters;

  @Mock
  private JobExecution jobExecution;

  @Mock
  private FileService fileService;

  @Mock
  private ApplicationReadyEvent applicationReadyEvent;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, fileService, jobRepository, exportServicePointJobService,
        exportTrafficPointElementJobService, exportLoadingPointJobService);
  }

  @Test
  public void shouldRecoverExportServicePointWhenNotAllJobsAreExecutedCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportServicePointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportServicePointWhenNotAllJobsAreExecutedJsonJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportServicePointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportTrafficPointElementWhenNotAllJobsAreExecutedCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportTrafficPointElementJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportTrafficPointElementWhenNotAllJobsAreExecutedJsonJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportTrafficPointElementJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportLoadingPointWhenNotAllJobsAreExecutedCsvJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportLoadingPointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportLoadingPointWhenNotAllJobsAreExecutedJsonJob() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME)).thenReturn(1L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME, 0, 12)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportLoadingPointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportServicePointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(6L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME, 0, 6)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportServicePointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportTrafficPointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)).thenReturn(6L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME, 0, 6)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportTrafficPointElementJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportLoadingPointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME)).thenReturn(6L);
    when(jobExplorer.getJobInstances(JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME, 0, 6)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportLoadingPointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldNotRecoverAnyJob() {
    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);
    //then
    verify(exportServicePointJobService, never()).startExportJobs();
    verify(exportTrafficPointElementJobService, never()).startExportJobs();
    verify(exportLoadingPointJobService, never()).startExportJobs();
    verify(fileService).clearDir();
  }

}
