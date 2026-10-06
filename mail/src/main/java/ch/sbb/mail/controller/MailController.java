package ch.sbb.mail.controller;

import ch.sbb.atlas.api.mail.MailApi;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController implements MailApi {

  private final MailService mailService;

  @Override
  public void produceMailNotification(MailNotification mailNotification) {
    mailService.sendEmailWithHtmlTemplate(mailNotification);
  }

}
