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
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("error", "Internal Server Error");
    expectedMailContent.put("correlationId", "abc123");

    doReturn("abc123").when(notificationService).getCurrentSpan();
    //when
    MailNotification result = notificationService.buildMailErrorNotification("export", stepExecution);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.SCHEDULING_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  @Test
  public void shouldBuildMailNotificationWhenThrowableIsNull() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("error", "No error details available");
    expectedMailContent.put("correlationId", "abc123");

    doReturn("abc123").when(notificationService).getCurrentSpan();
    //when
    MailNotification result = notificationService.buildMailErrorNotification("export", null);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.SCHEDULING_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenCurrentSpanIsNull() {
    //given
    when(tracer.currentSpan()).thenReturn(null);
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.buildMailErrorNotification("export", stepExecution));

  }

  @Test
  public void shouldThrowIllegalStateExceptionWhenSpanIsNull() {
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.getCurrentSpan());
  }

}