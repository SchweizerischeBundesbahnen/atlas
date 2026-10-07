package ch.sbb.atlas.api.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

class MailClientServiceTest {

  @Test
  void shouldDispatchAsynchronously() {
    MailApi mailApi = mock(MailApi.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("first@example.com"))
        .subject("Report")
        .build();
    AtomicReference<Runnable> dispatchedRequest = new AtomicReference<>();
    MailClientService service = new MailClientService(
        mailApi,
        JsonMapper.builder().build(),
        "http://localhost:8888",
        dispatchedRequest::set
    );

    service.produceMailNotification(notification);

    verifyNoInteractions(mailApi);

    dispatchedRequest.get().run();

    verify(mailApi).produceMailNotification(notification);
  }

  @Test
  void shouldLogRequestAndErrorResponseAndSuppressFailure() {
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
        "http://localhost:8888",
        Runnable::run
    );
    Logger logger = (Logger) LoggerFactory.getLogger(MailClientService.class);
    Level previousLevel = logger.getLevel();
    ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
    logAppender.start();
    logger.setLevel(Level.INFO);
    logger.addAppender(logAppender);

    try {
      assertThatNoException()
          .isThrownBy(() -> service.produceMailNotification(notification));

      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .first()
          .isEqualTo("Dispatching asynchronous mail request with subject Today's report to [receiver@example.com]");
      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .anySatisfy(message -> assertThat(message)
              .startsWith("Mail service request failed; the business operation will continue."));

      assertThat(service.createCurlCommand(notification))
          .contains("curl --fail-with-body --request POST 'http://localhost:8888/mail/internal/mail'")
          .contains("Authorization: ******")
          .contains("\"subject\":\"Today'\"'\"'s report\"");
    } finally {
      logger.detachAppender(logAppender);
      logger.setLevel(previousLevel);
      logAppender.stop();
    }
  }

}
