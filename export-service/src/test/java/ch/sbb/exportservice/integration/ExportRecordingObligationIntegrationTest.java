package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.integration.sql.BasePrmSqlIntegrationTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.SQLException;
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
public class ExportRecordingObligationIntegrationTest extends BasePrmSqlIntegrationTest {

  @MockitoBean
  private AmazonService amazonService;

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME)
  private Job exportRecordingObligationCsvJob;

  @BeforeEach
  void initData() throws SQLException {
    insertRecordingObligation("ch:1:sloid:70000", false);
  }

  @Test
  void shouldExecuteExportRecordingObligationCsvJob() throws Exception {
    // given
    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.FULL));
    // when
    JobExecution jobExecution = jobLauncher.run(exportRecordingObligationCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
