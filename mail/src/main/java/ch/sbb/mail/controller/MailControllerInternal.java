package ch.sbb.mail.controller;

import ch.sbb.atlas.api.mail.MailApiInternal;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailControllerInternal implements MailApiInternal {

  private final MailService mailService;

  @Override
  public void sendMailRequest(String mailReqId, MailNotification mailNotification) {
    try {
      mailService.sendEmailWithHtmlTemplate(mailNotification);
      log.info("mailReqId={}, status=MAIL_SEND_SUCCEEDED", mailReqId);
    } catch (RuntimeException exception) {
      log.error("mailReqId={}, status=MAIL_SEND_FAILED", mailReqId, exception);
      throw exception;
    }
  }

}
