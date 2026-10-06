package ch.sbb.atlas.api.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class MailClientServiceTest {

  @Test
  void shouldCreateManualRetryCommandAndSuppressFailure() {
    MailApi mailApi = mock(MailApi.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("receiver@example.com"))
        .subject("Today's report")
        .content("Content")
        .build();
    RuntimeException failure = new RuntimeException("Mail service unavailable");
    doThrow(failure).when(mailApi).produceMailNotification(notification);

    MailClientService service = new MailClientService(
        mailApi,
        JsonMapper.builder().build(),
        "http://localhost:8888"
    );

    assertThatNoException()
        .isThrownBy(() -> service.produceMailNotification(notification));

    assertThat(service.createCurlCommand(notification))
        .contains("curl --fail-with-body --request POST 'http://localhost:8888/mail/internal/mail'")
        .contains("Authorization: Bearer <ATLAS_ADMIN_TOKEN>")
        .contains("\"subject\":\"Today'\"'\"'s report\"");
  }

}
