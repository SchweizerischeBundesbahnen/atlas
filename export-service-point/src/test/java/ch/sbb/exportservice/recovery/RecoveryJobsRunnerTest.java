package ch.sbb.exportservice.recovery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
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

public class RecoveryJobsRunnerTest {

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
  @Qualifier(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  private Job exportServicePointCsvJob;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, jobLauncher, jobRepository, exportServicePointCsvJob, fileService);
  }

  @Test
  public void shouldRecoverExportServicePointCsvJob()
      throws Exception {
    //given
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.setId(132L);
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
    when(jobExplorer.getLastJobInstance(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher).run(eq(exportServicePointCsvJob), any());
    verify(fileService).clearDir();
  }

  @Test
  public void shouldNotRecoverAnyJob() throws Exception {
    //when
    recoveryJobsRunner.run(new DefaultApplicationArguments());
    //then
    verify(jobLauncher, never()).run(eq(exportServicePointCsvJob), any());
    verify(fileService).clearDir();
  }

}