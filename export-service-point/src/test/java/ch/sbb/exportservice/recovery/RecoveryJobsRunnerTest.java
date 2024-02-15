package ch.sbb.exportservice.recovery;

import static ch.sbb.exportservice.recovery.RecoveryJobsRunner.TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PLATFORM_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.service.ExportContactPointJobService;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.ExportToiletJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class RecoveryJobsRunnerTest {

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
  private ExportStopPointJobService exportStopPointJobService;

  @Mock
  private ExportPlatformJobService exportPlatformJobService;

  @Mock
  private ExportReferencePointJobService exportReferencePointJobService;

  @Mock
  private ExportContactPointJobService exportContactPointJobService;

  @Mock
  private ExportToiletJobService exportToiletJobService;

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
    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, fileService, jobRepository,
            exportServicePointJobService, exportTrafficPointElementJobService, exportLoadingPointJobService,
            exportStopPointJobService, exportPlatformJobService, exportReferencePointJobService, exportContactPointJobService, exportToiletJobService);
  }

  @Test
  void shouldRecoverExportServicePointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_SERVICE_POINT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
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
  void shouldRecoverExportTrafficPointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
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
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_LOADING_POINT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_LOADING_POINT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
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
  void shouldRecoverExportStopPointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_STOP_POINT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_STOP_POINT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportStopPointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportPlatformWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_PLATFORM_CSV_JOB_NAME)).thenReturn(Long.valueOf(
            TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_PLATFORM_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
            List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportPlatformJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportReferencePointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));

    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_REFERENCE_POINT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_REFERENCE_POINT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportReferencePointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldRecoverExportContactPointWhenOneJobIsNotSuccessfullyExecuted() throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(SePoDiExportType.WORLD_FULL.name(), String.class));

    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExecution.getCreateTime()).thenReturn(LocalDateTime.now());
    when(jobExplorer.getJobInstanceCount(EXPORT_CONTACT_POINT_CSV_JOB_NAME)).thenReturn(Long.valueOf(
        TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE));
    when(jobExplorer.getJobInstances(EXPORT_CONTACT_POINT_CSV_JOB_NAME, 0, TODAY_CSV_AND_JSON_EXPORTS_JOB_EXECUTION_SIZE)).thenReturn(
        List.of(jobInstance));
    when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(List.of(jobExecution));
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);

    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);

    //then
    verify(exportContactPointJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  void shouldNotRecoverAnyJob() {
    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);
    //then
    verify(exportServicePointJobService, never()).startExportJobs();
    verify(exportTrafficPointElementJobService, never()).startExportJobs();
    verify(exportLoadingPointJobService, never()).startExportJobs();
    verify(fileService).clearDir();
  }

}
