package ch.sbb.importservice.writer.geo;

import static ch.sbb.importservice.config.ServicePointGeoLocationUpdateConfig.UPDATE_SERVICE_POINT_GEO_JOB;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.client.ServicePointClient;
import ch.sbb.importservice.service.mail.MailProducerService;
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
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class GeoLocationUpdateJobIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Qualifier(UPDATE_SERVICE_POINT_GEO_JOB)
  @Autowired
  private Job updateServicePointGeoJob;

  @MockBean
  private ServicePointClient servicePointClient;

  @MockBean
  private MailProducerService mailProducerService;

  @Test
  void shouldExecuteUpdateGeoLocationJob() throws Exception {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(updateServicePointGeoJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(UPDATE_SERVICE_POINT_GEO_JOB);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    verify(mailProducerService, times(1)).produceMailNotification(any());
  }

}
