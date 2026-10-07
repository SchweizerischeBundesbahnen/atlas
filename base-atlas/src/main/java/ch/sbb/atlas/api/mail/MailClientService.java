package ch.sbb.atlas.api.mail;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class MailClientService {

  private static final String MAIL_PATH = "/mail/internal/mail";
  private static final ThreadFactory MAIL_THREAD_FACTORY = Thread.ofVirtual()
      .name("mail-client-", 0)
      .factory();
  private static final Executor MAIL_EXECUTOR = task -> MAIL_THREAD_FACTORY.newThread(task).start();

  private final MailApi mailApi;
  private final ObjectMapper objectMapper;
  private final String gatewayUrl;
  private final Executor mailExecutor;

  public MailClientService(MailApi mailApi, ObjectMapper objectMapper, String gatewayUrl) {
    this(mailApi, objectMapper, gatewayUrl, MAIL_EXECUTOR);
  }

  MailClientService(MailApi mailApi, ObjectMapper objectMapper, String gatewayUrl, Executor mailExecutor) {
    this.mailApi = mailApi;
    this.objectMapper = objectMapper;
    this.gatewayUrl = gatewayUrl;
    this.mailExecutor = mailExecutor;
  }

  public void produceMailNotification(MailNotification mailNotification) {
    log.info("Dispatching asynchronous mail request with subject {} to {}",
        mailNotification.getSubject(), mailNotification.getTo());
    try {
      mailExecutor.execute(() -> send(mailNotification));
    } catch (RuntimeException exception) {
      logFailure(mailNotification, exception);
    }
  }

  private void send(MailNotification mailNotification) {
    try {
      mailApi.produceMailNotification(mailNotification);
    } catch (RuntimeException exception) {
      logFailure(mailNotification, exception);
    }
  }

  private void logFailure(MailNotification mailNotification, RuntimeException exception) {
    log.error("Mail service request failed; the business operation will continue. Retry manually with:\n{}",
        createCurlCommand(mailNotification), exception);
  }

  String createCurlCommand(MailNotification mailNotification) {
    try {
      String payload = objectMapper.writeValueAsString(mailNotification);
      return "curl --fail-with-body --request POST "
          + shellQuote(gatewayUrl + MAIL_PATH)
          + " --header 'Authorization: ******'"
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
