package ch.sbb.atlas.api.mail;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("internal")
public interface MailApiInternal {

  String MAIL_REQUEST_ID_HEADER = "Mail-Request-Id";

  @PostMapping("send")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void sendMailRequest(
      @RequestHeader(MAIL_REQUEST_ID_HEADER) String mailReqId,
      @Valid @RequestBody MailNotification mailNotification);

}
