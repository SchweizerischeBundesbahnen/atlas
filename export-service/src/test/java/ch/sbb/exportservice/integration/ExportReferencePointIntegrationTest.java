package ch.sbb.exportservice.integration;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.integration.sql.BasePrmSqlIntegrationTest;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.BaseExportJobService;
import ch.sbb.exportservice.service.BaseExportJobService.JobParams;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_REFERENCE_POINT_JSON_JOB_NAME;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@AutoConfigureMockMvc(addFilters = false)
class ExportReferencePointIntegrationTest extends BasePrmSqlIntegrationTest {

  @MockitoBean
  private AmazonService amazonService;

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_REFERENCE_POINT_CSV_JOB_NAME)
  private Job exportReferencePointCsvJob;

  @Autowired
  @Qualifier(EXPORT_REFERENCE_POINT_JSON_JOB_NAME)
  private Job exportReferencePointJsonJob;

  @BeforeEach
  void initData() throws SQLException {
    insertStopPoint(8507000, "ch:1:sloid:70000", LocalDate.now(), LocalDate.now());
  }

  @Test
  void shouldExecuteExportReferencePointCsvJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL, PrmExportType.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportReferencePointCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_REFERENCE_POINT_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteExportReferencePointJsonJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL, PrmExportType.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportReferencePointJsonJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_REFERENCE_POINT_JSON_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
