package ch.sbb.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MailNotificationServiceTest {

  @Mock
  private Tracer tracer;

  @InjectMocks
  @Spy
  private MailNotificationService notificationService;

  @Test
   void shouldBuildMailNotification() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("error", "Internal Server Error");
    expectedMailContent.put("correlationId", "abc123");

    Throwable throwable = new Throwable("Internal Server Error");
    doReturn("abc123").when(notificationService).getCurrentSpan();
    //when
    MailNotification result = notificationService.buildMailNotification("export", throwable);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.SCHEDULING_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  @Test
   void shouldBuildMailNotificationWhenThrowableIsNull() {
    //given
    Map<String, Object> expectedMailContent = new HashMap<>();
    expectedMailContent.put("jobName", "export");
    expectedMailContent.put("error", "No error details available");
    expectedMailContent.put("correlationId", "abc123");

    doReturn("abc123").when(notificationService).getCurrentSpan();
    //when
    MailNotification result = notificationService.buildMailNotification("export", null);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.SCHEDULING_ERROR_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo("Job [export] execution failed");
    assertThat(result.getTemplateProperties()).isNotEmpty();
    assertThat(result.getTemplateProperties()).containsOnly(expectedMailContent);
  }

  @Test
   void shouldThrowIllegalStateExceptionWhenCurrentSpanIsNull() {
    //given
    when(tracer.currentSpan()).thenReturn(null);
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.buildMailNotification("export", new Throwable("Error")));

  }

  @Test
   void shouldThrowIllegalStateExceptionWhenSpanIsNull() {
    //when
    assertThrows(IllegalStateException.class, () -> notificationService.getCurrentSpan());
  }

}
