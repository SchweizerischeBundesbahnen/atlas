package ch.sbb.mail.controller;

import ch.sbb.mail.api.MailApiV1;
import ch.sbb.mail.model.MailNotification;
import ch.sbb.mail.service.MailService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class MailController implements MailApiV1 {

  private final MailService mailService;

  @Override
  public ResponseEntity<?> sendEmail(@Valid MailNotification mailNotification) {
    mailService.sendSimpleMail(mailNotification);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<?> sendEmailInHtml(@Valid MailNotification mailNotification) {
    mailService.sendEmailWithHtmlTemplate(mailNotification);
    return ResponseEntity.ok().build();
  }
}
