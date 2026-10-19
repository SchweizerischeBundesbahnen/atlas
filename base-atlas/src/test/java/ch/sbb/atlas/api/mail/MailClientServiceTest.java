package ch.sbb.atlas.api.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class MailClientServiceTest {

  @Test
  void shouldDispatchAsynchronously() {
    MailApiInternal mailApi = mock(MailApiInternal.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("first@example.com"))
        .subject("Report")
        .build();
    AtomicReference<Runnable> dispatchedRequest = new AtomicReference<>();
    MailClientService service = new MailClientService(
        mailApi,
        dispatchedRequest::set
    );

    service.sendMailRequest(notification);

    verifyNoInteractions(mailApi);

    dispatchedRequest.get().run();

    verify(mailApi).sendMailRequest(anyString(), eq(notification));
  }

  @Test
  void shouldDispatchOnlyAfterTransactionCommit() {
    MailApiInternal mailApi = mock(MailApiInternal.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("first@example.com"))
        .subject("Report")
        .build();
    AtomicReference<Runnable> dispatchedRequest = new AtomicReference<>();
    MailClientService service = new MailClientService(
        mailApi,
        dispatchedRequest::set
    );

    TransactionSynchronizationManager.setActualTransactionActive(true);
    TransactionSynchronizationManager.initSynchronization();
    try {
      service.sendMailRequest(notification);

      assertThat(dispatchedRequest).hasNullValue();

      TransactionSynchronizationManager.getSynchronizations().get(0).afterCommit();

      assertThat(dispatchedRequest).doesNotHaveNullValue();
      dispatchedRequest.get().run();
      verify(mailApi).sendMailRequest(anyString(), eq(notification));
    } finally {
      TransactionSynchronizationManager.clearSynchronization();
      TransactionSynchronizationManager.setActualTransactionActive(false);
    }
  }

  @Test
  void shouldNotDispatchAfterTransactionRollback() {
    MailApiInternal mailApi = mock(MailApiInternal.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("first@example.com"))
        .subject("Report")
        .build();
    AtomicReference<Runnable> dispatchedRequest = new AtomicReference<>();
    MailClientService service = new MailClientService(
        mailApi,
        dispatchedRequest::set
    );

    TransactionSynchronizationManager.setActualTransactionActive(true);
    TransactionSynchronizationManager.initSynchronization();
    try {
      service.sendMailRequest(notification);

      TransactionSynchronizationManager.getSynchronizations().get(0)
          .afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

      assertThat(dispatchedRequest).hasNullValue();
      verifyNoInteractions(mailApi);
    } finally {
      TransactionSynchronizationManager.clearSynchronization();
      TransactionSynchronizationManager.setActualTransactionActive(false);
    }
  }

  @Test
  void shouldLogRequestAndErrorResponseAndSuppressFailure() {
    MailApiInternal mailApi = mock(MailApiInternal.class);
    MailNotification notification = MailNotification.builder()
        .to(List.of("receiver@example.com"))
        .subject("Today's report")
        .content("Content")
        .build();
    RuntimeException failure = new RuntimeException("Mail service unavailable");
    doThrow(failure).when(mailApi).sendMailRequest(anyString(), eq(notification));

    MailClientService service = new MailClientService(
        mailApi,
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
          .isThrownBy(() -> service.sendMailRequest(notification));

      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .first()
          .asString()
          .matches("mailReqId=[0-9a-f-]{36}, status=MAIL_REQ_STARTED, "
              + "subject=Today's report, to=\\[receiver@example.com\\]");
      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .anySatisfy(message -> assertThat(message)
              .contains("Mail service request failed; the business operation will continue.")
              .contains("subject=Today's report")
              .contains("to=[receiver@example.com]"));
    } finally {
      logger.detachAppender(logAppender);
      logger.setLevel(previousLevel);
      logAppender.stop();
    }
  }

}
