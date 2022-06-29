package ch.sbb.mail.api;

import ch.sbb.mail.model.MailNotification;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Mails")
@RequestMapping("v1/mail")
public interface MailApiV1 {

  @PostMapping(value = "simple")
  ResponseEntity<?> sendEmail(@RequestBody MailNotification mailNotification);

  @PostMapping(value = "html")
  ResponseEntity<?> sendEmailInHtml(@RequestBody MailNotification mailNotification);

}
