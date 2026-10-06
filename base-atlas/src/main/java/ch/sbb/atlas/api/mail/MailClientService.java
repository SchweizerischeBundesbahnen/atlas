package ch.sbb.atlas.api.mail;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class MailClientService {

  private static final String MAIL_PATH = "/mail/internal/mail";

  private final MailApi mailApi;
  private final ObjectMapper objectMapper;
  private final String gatewayUrl;

  public MailClientService(MailApi mailApi, ObjectMapper objectMapper, String gatewayUrl) {
    this.mailApi = mailApi;
    this.objectMapper = objectMapper;
    this.gatewayUrl = gatewayUrl;
  }

  public void produceMailNotification(MailNotification mailNotification) {
    try {
      mailApi.produceMailNotification(mailNotification);
    } catch (RuntimeException exception) {
      log.error("Synchronous mail request failed; the business operation will continue. Retry manually with:\n{}",
          createCurlCommand(mailNotification), exception);
    }
  }

  String createCurlCommand(MailNotification mailNotification) {
    try {
      String payload = objectMapper.writeValueAsString(mailNotification);
      return "curl --fail-with-body --request POST "
          + shellQuote(gatewayUrl + MAIL_PATH)
          + " --header \"Authorization: Bearer <ATLAS_ADMIN_TOKEN>\""
          + " --header 'Content-Type: application/json'"
          + " --data-raw " + shellQuote(payload);
    } catch (JacksonException exception) {
      log.error("Unable to serialize failed mail request for manual retry. Mail payload: {}", mailNotification, exception);
      return "Unable to create curl command; see the mail payload in the preceding log entry.";
    }
  }

  private String shellQuote(String value) {
    return "'" + value.replace("'", "'\"'\"'") + "'";
  }

}
