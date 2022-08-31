package ch.sbb.scheduling.service;

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

  @Override
  public <T, E extends Throwable> void close(RetryContext context,
      RetryCallback<T, E> callback, Throwable throwable) {
    if (throwable != null) {
      log.error("Unable to recover job {} from  Exception",
          ((MethodInvocationRetryCallback<?, ?>) callback).getLabel());
      log.error("Sending Mail notification...");
      super.close(context, callback, throwable);
    }
  }

  @Override
  public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
      Throwable throwable) {
    if (throwable != null) {
      log.error("Exception Occurred on {}, Retry Count {} ",
          ((MethodInvocationRetryCallback<?, ?>) callback).getLabel(), context.getRetryCount());
      super.onError(context, callback, throwable);
    }
  }
  
}