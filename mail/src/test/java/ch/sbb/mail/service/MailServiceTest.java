package ch.sbb.mail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.mail.exception.MailSendException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

class MailServiceTest {

  private final JavaMailSender javaMailSender = mock(JavaMailSender.class);
  private final MailContentBuilder mailContentBuilder = mock(MailContentBuilder.class);
  private final MailService mailService = new MailService(javaMailSender, mailContentBuilder);

  @Test
  void shouldLogAndPropagateHtmlMailFailure() {
    doThrow(new org.springframework.mail.MailSendException("SMTP unavailable"))
        .when(javaMailSender).send(any(MimeMessagePreparator.class));

    MailNotification notification = MailNotification.builder()
        .to(List.of("receiver@example.com"))
        .subject("Subject")
        .build();
    Logger logger = (Logger) LoggerFactory.getLogger(MailService.class);
    Level previousLevel = logger.getLevel();
    ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
    logAppender.start();
    logger.setLevel(Level.ERROR);
    logger.addAppender(logAppender);

    try {
      assertThatThrownBy(() -> mailService.sendEmailWithHtmlTemplate(notification))
          .isInstanceOf(MailSendException.class)
          .hasMessage("SMTP unavailable");

      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .containsExactly("Mail " + notification + " not sent.");
    } finally {
      logger.detachAppender(logAppender);
      logger.setLevel(previousLevel);
      logAppender.stop();
    }
  }

}
