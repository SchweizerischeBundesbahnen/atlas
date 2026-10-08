package ch.sbb.importservice.module.geo.writer;

import static ch.sbb.importservice.module.geo.job.ServicePointGeoLocationUpdateConfig.UPDATE_SERVICE_POINT_GEO_JOB;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.config.mail.MailClient;
import ch.sbb.importservice.module.geo.client.ServicePointClient;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class GeoLocationUpdateJobIntegrationTest {

  @Autowired
  private JobOperator jobOperator;

  @Qualifier(UPDATE_SERVICE_POINT_GEO_JOB)
  @Autowired
  private Job updateServicePointGeoJob;

  @MockitoBean
  private ServicePointClient servicePointClient;

  @MockitoBean
  private MailClient mailClient;

  @Test
  void shouldExecuteUpdateGeoLocationJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobOperator.start(updateServicePointGeoJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(UPDATE_SERVICE_POINT_GEO_JOB);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    verify(mailClient, times(1)).sendMailRequest(any());
  }

}
