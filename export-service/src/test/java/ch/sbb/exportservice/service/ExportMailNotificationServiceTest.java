package ch.sbb.exportservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

class ExportMailNotificationServiceTest {

  private MailNotificationService notificationService;

  @BeforeEach
   void setUp() {
    notificationService = new MailNotificationService();
  }

  @Test
   void shouldBuildMailNotification() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    expectedMailContent.put("stepName", "myStep");
    expectedMailContent.put("stepExecutionInformation", "Step [myStep with id 123] executed in ");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.getExecutionContext().put("traceId", "abc123");
    stepExecution.setId(123L);
    //when
    MailNotification result = notificationService.buildMailErrorNotification("export", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  @Test
   void shouldBuildMailNotificationWhenThrowableIsNull() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    expectedMailContent.put("stepName", "myStep");
    expectedMailContent.put("stepExecutionInformation", "Step [myStep with id 123] executed in ");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.getExecutionContext().put("traceId", "abc123");
    stepExecution.setId(123L);
    //when
    MailNotification result = notificationService.buildMailErrorNotification("export", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

}
