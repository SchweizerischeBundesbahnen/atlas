package ch.sbb.exportservice.job;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

class BaseExportJobServiceTest {

  private BaseExportJobService baseExportJobService;
  private JobLauncher jobLauncherMock;
  private Job jobMock;

  @BeforeEach
  void setUp() {
    jobLauncherMock = Mockito.mock(JobLauncher.class);
    jobMock = Mockito.mock(Job.class);
    baseExportJobService = new BaseExportJobService(jobLauncherMock, jobMock, jobMock) {
      @Override
      protected List<JobParams> getExportTypes() {
        return List.of(new JobParams(ExportTypeV2.FULL));
      }
    };
  }

  @Test
  void startExportJobsAsync()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException,
      JobRestartException {
    // given
    final JobExecution jobExecutionMock = Mockito.mock(JobExecution.class);
    Mockito.when(jobExecutionMock.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
    Mockito.when(jobLauncherMock.run(Mockito.same(jobMock), Mockito.any(JobParameters.class))).thenReturn(jobExecutionMock);

    // when
    final CompletableFuture<Boolean> result = baseExportJobService.startExportJobsAsync();

    // then
    assertThat(result).isCompletedWithValue(true);
    Mockito.verify(jobLauncherMock, Mockito.times(2)).run(Mockito.same(jobMock), Mockito.any(JobParameters.class));
  }

}
