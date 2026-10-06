package ch.sbb.atlas.api.mail;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface MailApi {

  @PostMapping("/internal/mail")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void produceMailNotification(@Valid @RequestBody MailNotification mailNotification);

}
