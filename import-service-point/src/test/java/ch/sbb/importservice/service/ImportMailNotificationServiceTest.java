package ch.sbb.importservice.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ImportMailNotificationServiceTest {

  @InjectMocks
  @Spy
  private MailNotificationService notificationService;

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
    expectedMailContent.put("stepExecutionInformation", "Step [myStep with id 123] executed in ");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.getExecutionContext().put("traceId", "abc123");
    stepExecution.setId(123L);
    //when
    MailNotification result = notificationService.buildMailErrorNotification("import", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION);
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
    expectedMailContent.put("stepExecutionInformation", "Step [myStep with id 123] executed in ");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    stepExecution.getExecutionContext().put("traceId", "abc123");
    stepExecution.setId(123L);
    //when
    MailNotification result = notificationService.buildMailErrorNotification("import", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [import] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

}