package ch.sbb.importservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.cloud.sleuth.Tracer;

@ExtendWith(MockitoExtension.class)
class ImportMailNotificationServiceTest {

  @Mock
  private Tracer tracer;

  @Mock
  private StepExecution stepExecution;

  @InjectMocks
  @Spy
  private MailNotificationService notificationService;

  @Test
  public void shouldBuildMailNotification() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "import");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);

    doReturn("abc123").when(notificationService).getCurrentSpan();
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
  public void shouldBuildMailNotificationWhenThrowableIsNull() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "import");
    expectedMailContent.put("cause", "");
    expectedMailContent.put("correlationId", "abc123");
    expectedMailContent.put("exception", "");
    expectedMailContent.put("jobParameter", "{}");
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);

    doReturn("abc123").when(notificationService).getCurrentSpan();
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
  public void shouldThrowIllegalStateExceptionWhenCurrentSpanIsNull() {
    //given
    when(tracer.currentSpan()).thenReturn(null);
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.buildMailErrorNotification("import", stepExecution));

  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenSpanIsNull() {
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.getCurrentSpan());
  }

}