package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class ExportTrafficPointElementIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)
  private Job exportTrafficPointElementCsvJob;

  @Autowired
  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME)
  private Job exportTrafficPointElementJsonJob;

  @Test
  public void shouldExecuteExportServicePointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, ExportType.WORLD_FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(exportTrafficPointElementCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  public void shouldExecuteExportServicePointJsonJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, ExportType.WORLD_FULL.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(exportTrafficPointElementJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
