package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.mail.MailNotification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @deprecated "This REST Client will be deleted when ATLAS integrates Kafka for Service to Service communication"
 */
@FeignClient(name = "mailClient", url = "${external.url.mail}/v1/mail")
public interface MailClient {

  @PostMapping(value = "html")
  ResponseEntity<?> sendEmailInHtml(@RequestBody MailNotification mailNotification);

}