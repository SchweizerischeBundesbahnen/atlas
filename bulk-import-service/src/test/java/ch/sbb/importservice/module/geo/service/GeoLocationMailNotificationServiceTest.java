package ch.sbb.importservice.module.geo.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.step.StepExecution;

@ExtendWith(MockitoExtension.class)
class GeoLocationMailNotificationServiceTest {

 @InjectMocks
 @Spy
 private GeoLocationMailNotificationService notificationService;

 @Test
 void shouldBuildMailNotification() {
  //given
  Map<String, Object> expectedMailContent = new HashMap<>();
  expectedMailContent.put("jobName", "import");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    expectedMailContent.put("stepName", "myStep");
  expectedMailContent.put("stepExecutionInformation", "Job [job with id 1] executed in ");
  JobExecution jobExecution = new JobExecution(1L, new JobInstance(1L, "job"), new JobParameters());
  StepExecution stepExecution = new StepExecution(123,"myStep", jobExecution);
  stepExecution.getExecutionContext().put("traceId", "abc123");
  //when
  MailNotification result = notificationService.buildMailErrorNotification("import", stepExecution);

  //then
  assertThat(result).isNotNull();
  assertThat(result.getMailType()).isEqualTo(MailType.UPDATE_GEOLOCATION_ERROR_NOTIFICATION);
  assertThat(result.getSubject()).isEqualTo("Job [import] execution failed");
  assertThat(result.getTemplateProperties()).isNotEmpty();
  assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
 }

  @Test
   void shouldBuildMailNotificationWhenThrowableIsNull() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "import");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
   expectedMailContent.put("stepName", "myStep");
   expectedMailContent.put("stepExecutionInformation", "Job [job with id 1] executed in ");
   JobExecution jobExecution = new JobExecution(1L, new JobInstance(1L, "job"), new JobParameters());
   StepExecution stepExecution = new StepExecution(123L,"myStep", jobExecution);
   stepExecution.getExecutionContext().put("traceId", "abc123");
   //when
   MailNotification result = notificationService.buildMailErrorNotification("import", stepExecution);

   //then
   assertThat(result).isNotNull();
   assertThat(result.getMailType()).isEqualTo(MailType.UPDATE_GEOLOCATION_ERROR_NOTIFICATION);
   assertThat(result.getSubject()).isEqualTo("Job [import] execution failed");
   assertThat(result.getTemplateProperties()).isNotEmpty();
   assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

}