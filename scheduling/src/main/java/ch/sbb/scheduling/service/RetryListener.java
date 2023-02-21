package ch.sbb.scheduling.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.interceptor.MethodInvocationRetryCallback;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RetryListener extends RetryListenerSupport {

  private final MailProducerService service;

  private final MailNotificationService mailNotificationService;

  @Override
  public <T, E extends Throwable> void close(RetryContext context,
      RetryCallback<T, E> callback, Throwable throwable) {
    if (throwable != null) {
      String jobName = getJobName(callback);
      log.error("Unable to recover job {} from  Exception", jobName, throwable);
      log.error("Sending Mail notification...");
      MailNotification mailNotification = mailNotificationService.buildMailNotification(getJobName(callback), throwable);
      service.produceMailNotification(mailNotification);
      super.close(context, callback, throwable);
    }
  }

  @Override
  public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
      Throwable throwable) {
    if (throwable != null) {
      log.error("Exception Occurred on {}, Retry Count {} ", getJobName(callback), context.getRetryCount(), throwable);
      super.onError(context, callback, throwable);
    }
  }

  <T, E extends Throwable> String getJobName(RetryCallback<T, E> callback) {
    return ((MethodInvocationRetryCallback<?, ?>) callback).getLabel();
  }

}
