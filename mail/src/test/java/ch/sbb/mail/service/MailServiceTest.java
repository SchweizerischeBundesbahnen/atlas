package ch.sbb.mail.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.mail.exception.MailSendException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

class MailServiceTest {

  private final JavaMailSender javaMailSender = org.mockito.Mockito.mock(JavaMailSender.class);
  private final MailContentBuilder mailContentBuilder = org.mockito.Mockito.mock(MailContentBuilder.class);
  private final MailService mailService = new MailService(javaMailSender, mailContentBuilder);

  @Test
  void shouldPropagateHtmlMailFailure() {
    doThrow(new org.springframework.mail.MailSendException("SMTP unavailable"))
        .when(javaMailSender).send(any(MimeMessagePreparator.class));

    MailNotification notification = MailNotification.builder()
        .to(List.of("receiver@example.com"))
        .subject("Subject")
        .build();

    assertThatThrownBy(() -> mailService.sendEmailWithHtmlTemplate(notification))
        .isInstanceOf(MailSendException.class)
        .hasMessage("SMTP unavailable");
  }

}
