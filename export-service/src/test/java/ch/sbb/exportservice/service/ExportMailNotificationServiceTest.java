package ch.sbb.exportservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.step.StepExecution;

class ExportMailNotificationServiceTest {

  private MailNotificationService notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new MailNotificationService();
  }

  @Test
  void shouldBuildMailNotification() {
    //given
    Map<String, Object> expectedMailContent = getMailContent();
    StepExecution stepExecution = getStepExecution();

    //when
    MailNotification result = notificationService.buildMailErrorNotification("export", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  private static @NotNull StepExecution getStepExecution() {
    JobExecution jobExecution = new JobExecution(1L, new JobInstance(1L, "job"), new JobParameters());
    StepExecution stepExecution = new StepExecution(123L, "myStep", jobExecution);
    stepExecution.getExecutionContext().put("traceId", "abc123");
    return stepExecution;
  }

  private static @NotNull Map<String, Object> getMailContent() {
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    expectedMailContent.put("stepName", "myStep");
    expectedMailContent.put("stepExecutionInformation", "Step [myStep with id 123] executed in ");
    return expectedMailContent;
  }
}
