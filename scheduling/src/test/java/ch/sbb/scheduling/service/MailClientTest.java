package ch.sbb.scheduling.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.mail.MailNotification;
import org.junit.jupiter.api.Test;

class MailClientTest {

  @Test
  void shouldDelegateMailRequestToFeignClient() {
    MailFeignClient mailFeignClient = mock(MailFeignClient.class);
    MailNotification mailNotification = MailNotification.builder()
        .subject("Import result")
        .build();
    MailClient mailClient = new MailClient(mailFeignClient, Runnable::run);

    mailClient.sendMailRequest(mailNotification);

    verify(mailFeignClient).sendMailRequest(anyString(), eq(mailNotification));
  }
}
