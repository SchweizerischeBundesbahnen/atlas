package ch.sbb.atlas.api.mail;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
public class MailClientService {

  private static final String MAIL_PATH = "/mail/internal/send";

  private final MailApiInternal mailApi;
  private final ObjectMapper objectMapper;
  private final String gatewayUrl;
  private final TaskExecutor mailTaskExecutor;

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
    log.error("mailReqId={}, Mail service request failed; the business operation will continue. subject={}, to={}",
        mailReqId, mailNotification.getSubject(), mailNotification.getTo(), exception);
  }

}
