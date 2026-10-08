package ch.sbb.atlas.api.mail;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class MailClientService {

  private static final String MAIL_PATH = "/mail/internal/mail";

  private final MailApi mailApi;
  private final ObjectMapper objectMapper;
  private final String gatewayUrl;
  private final TaskExecutor mailTaskExecutor;

  public MailClientService(MailApi mailApi, ObjectMapper objectMapper, String gatewayUrl,
      TaskExecutor mailTaskExecutor) {
    this.mailApi = mailApi;
    this.objectMapper = objectMapper;
    this.gatewayUrl = gatewayUrl;
    this.mailTaskExecutor = mailTaskExecutor;
  }

  public void sendMailRequest(MailNotification mailNotification) {
    String mailReqId = UUID.randomUUID().toString();
    log.info("mailReqId={}, status=MAIL_REQ_STARTED, subject={}, to={}",
        mailReqId, mailNotification.getSubject(), mailNotification.getTo());
    try {
      mailTaskExecutor.execute(() -> send(mailReqId, mailNotification));
    } catch (RuntimeException exception) {
      logFailure(mailReqId, mailNotification, exception);
    }
  }

  private void send(String mailReqId, MailNotification mailNotification) {
    try {
      mailApi.sendMailRequest(mailReqId, mailNotification);
    } catch (RuntimeException exception) {
      logFailure(mailReqId, mailNotification, exception);
    }
  }

  private void logFailure(String mailReqId, MailNotification mailNotification, RuntimeException exception) {
    log.error("mailReqId={}, Mail service request failed; the business operation will continue. Retry manually with:\n{}",
        mailReqId, createCurlCommand(mailReqId, mailNotification), exception);
  }

  String createCurlCommand(String mailReqId, MailNotification mailNotification) {
    try {
      String payload = objectMapper.writeValueAsString(mailNotification);
      return "curl --fail-with-body --request POST "
          + shellQuote(gatewayUrl + MAIL_PATH)
          + " --header 'Authorization: ******'"
          + " --header " + shellQuote(MailApi.MAIL_REQUEST_ID_HEADER + ": " + mailReqId)
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
