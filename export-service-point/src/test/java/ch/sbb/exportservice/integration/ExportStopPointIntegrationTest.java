package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_STOP_POINT_JSON_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc(addFilters = false)
class ExportStopPointIntegrationTest extends BasePrmSqlIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_STOP_POINT_CSV_JOB_NAME)
  private Job exportStopPointCsvJob;

  @Autowired
  @Qualifier(EXPORT_STOP_POINT_JSON_JOB_NAME)
  private Job exportStopPointJsonJob;

  @BeforeEach
  void initData() throws SQLException {
   insertStopPoint(8507000, "ch:1:sloid:70000", LocalDate.now(),LocalDate.now());
  }

  @Test
  void shouldExecuteExportStopPointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, PrmExportType.FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(exportStopPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_STOP_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteExportStopPointJsonJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, PrmExportType.FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(exportStopPointJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_STOP_POINT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
