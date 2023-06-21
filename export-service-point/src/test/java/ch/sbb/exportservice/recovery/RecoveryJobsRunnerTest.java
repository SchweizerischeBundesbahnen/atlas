package ch.sbb.exportservice.recovery;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.service.ExportJobService;
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

public class RecoveryJobsRunnerTest {

  private RecoveryJobsRunner recoveryJobsRunner;
  @Mock
  private JobExplorer jobExplorer;

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private ExportJobService exportJobService;

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
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, fileService, jobRepository, exportJobService);
  }

  @Test
  public void shouldRecoverExportServiceWhenNotAllJobsAreExecutedPointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ServicePointExportType.WORLD_FULL.name(), String.class));
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
    verify(exportJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldRecoverExportServiceWhenOneJobIsNotSuccessfullyExecuted()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    parameters.put(EXPORT_TYPE_JOB_PARAMETER, new JobParameter<>(ServicePointExportType.WORLD_FULL.name(), String.class));
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
    verify(exportJobService).startExportJobs();
    verify(fileService).clearDir();
  }

  @Test
  public void shouldNotRecoverAnyJob() {
    //when
    recoveryJobsRunner.onApplicationEvent(applicationReadyEvent);
    //then
    verify(exportJobService, never()).startExportJobs();
    verify(fileService).clearDir();
  }

}