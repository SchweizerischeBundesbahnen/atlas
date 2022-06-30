package ch.sbb.mail.api;

import ch.sbb.atlas.model.mail.MailNotification;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @deprecated
 * "This REST API will be deleted when ATLAS integrates Kafka for Service to Service communication"
 */
@Tag(name = "Mails")
@RequestMapping("v1/mail")
@Deprecated(forRemoval = true)
public interface MailApiV1 {

  /**
   * @deprecated
   * "This REST API will be deleted when ATLAS integrates Kafka for Service to Service communication"
   */
  @Deprecated(forRemoval = true)
  @PostMapping(value = "simple")
  ResponseEntity<?> sendEmail(@RequestBody MailNotification mailNotification);

  /**
   * @deprecated
   * "This REST API will be deleted when ATLAS integrates Kafka for Service to Service communication"
   */
  @Deprecated(forRemoval = true)
  @PostMapping(value = "html")
  ResponseEntity<?> sendEmailInHtml(@RequestBody MailNotification mailNotification);

}
