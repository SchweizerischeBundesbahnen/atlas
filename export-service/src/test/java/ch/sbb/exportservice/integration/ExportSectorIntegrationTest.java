package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTOR_JSON_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
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
public class ExportSectorIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_SECTOR_JSON_JOB_NAME)
  private Job exportSectorJsonJob;

  @Test
  void shouldExecuteExportServicePointJsonJob() throws Exception {
    // given

    JobParameters jobParameters = BaseExportJobService.buildJobParameters(
        new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportSectorJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_SECTOR_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
