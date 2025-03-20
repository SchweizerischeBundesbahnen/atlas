package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_STOP_POINT_JSON_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.integration.sql.BasePrmSqlIntegrationTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
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
    insertStopPoint(8507000, "ch:1:sloid:70000", LocalDate.now(), LocalDate.now());
  }

  @Test
  void shouldExecuteExportStopPointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL, PrmExportType.FULL));
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
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL, PrmExportType.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportStopPointJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_STOP_POINT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
