package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.SePoDiExportType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class ExportLoadingPointIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME)
  private Job exportLoadingPointCsvJob;

  @Autowired
  @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME)
  private Job exportLoadingPointJsonJob;

  @Test
  void shouldExecuteExportLoadingPointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(
        new JobParams(ExportTypeV2.WORLD_FULL, SePoDiExportType.WORLD_FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportLoadingPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_LOADING_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteExportLoadingPointJsonJob() throws Exception {
    // given

    JobParameters jobParameters = BaseExportJobService.buildJobParameters(
        new JobParams(ExportTypeV2.WORLD_FULL, SePoDiExportType.WORLD_FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportLoadingPointJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_LOADING_POINT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
