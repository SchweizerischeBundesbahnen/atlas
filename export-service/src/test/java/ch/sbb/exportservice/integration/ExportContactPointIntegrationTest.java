package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_CONTACT_POINT_JSON_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.s3.service.AmazonService;
import ch.sbb.exportservice.integration.sql.BasePrmSqlIntegrationTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@AutoConfigureMockMvc(addFilters = false)
class ExportContactPointIntegrationTest extends BasePrmSqlIntegrationTest {

  @MockitoBean
  private AmazonService amazonService;

  @Autowired
  private JobOperator jobOperator;

  @Autowired
  @Qualifier(EXPORT_CONTACT_POINT_CSV_JOB_NAME)
  private Job exportContactPointCsvJob;

  @Autowired
  @Qualifier(EXPORT_CONTACT_POINT_JSON_JOB_NAME)
  private Job exportContactPointJsonJob;

  @BeforeEach
  void initData() throws SQLException {
    insertStopPoint(1000, 8507000, "ch:1:sloid:70000", LocalDate.now(), LocalDate.now());
  }

  @Test
  void shouldExecuteExportContactPointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobOperator.start(exportContactPointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_CONTACT_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteExportContactPointJsonJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobOperator.start(exportContactPointJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_CONTACT_POINT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
