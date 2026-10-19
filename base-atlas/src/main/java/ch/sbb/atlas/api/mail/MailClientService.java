package ch.sbb.atlas.api.mail;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
public class MailClientService {

  private final MailApiInternal mailApi;
  private final TaskExecutor mailTaskExecutor;

  public void sendMailRequest(MailNotification mailNotification) {
    String mailReqId = UUID.randomUUID().toString();
    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      scheduleAfterCommit(mailReqId, mailNotification);
      return;
    }
    dispatch(mailReqId, mailNotification);
  }

  private void scheduleAfterCommit(String mailReqId, MailNotification mailNotification) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      // Splunk alert(s) could be dependent on this log message
      log.error("mailReqId={}, Mail request not scheduled because transaction synchronization is not active. "
              + "The business operation will continue. subject={}, to={}",
          mailReqId, mailNotification.getSubject(), mailNotification.getTo());
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        dispatch(mailReqId, mailNotification);
      }
    });
  }

  private void dispatch(String mailReqId, MailNotification mailNotification) {
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
