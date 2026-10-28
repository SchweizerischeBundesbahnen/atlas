package ch.sbb.atlas.api.client.bodi;

import ch.sbb.atlas.annotation.RetryableFeignRequest;
import feign.MethodMetadata;
import feign.Request;
import feign.Request.HttpMethod;
import feign.RequestTemplate;
import feign.RetryableException;
import feign.Retryer;
import java.lang.reflect.Method;
import java.util.Optional;

class TransportCompanyRetryer implements Retryer {

  private static final int MAX_RETRIES = 2;
  private static final long BACKOFF_MILLIS = 1000;

  private int retries;

  static boolean isRetryableRequest(Request request) {
    Optional<Method> retryableAnnotation = Optional.ofNullable(request.requestTemplate())
        .map(RequestTemplate::methodMetadata)
        .map(MethodMetadata::method)
        .filter(method -> method.isAnnotationPresent(RetryableFeignRequest.class));
    
    return request.httpMethod() == HttpMethod.GET && retryableAnnotation.isPresent();
  }

  @Override
  public void continueOrPropagate(RetryableException exception) {
    if (!isRetryableRequest(exception.request()) || retries >= MAX_RETRIES) {
      throw exception;
    }
    // Do not retry earlier than Retry-After or extend the synchronous request's backoff budget.
    if (exception.retryAfter() != null && exception.retryAfter() > System.currentTimeMillis() + BACKOFF_MILLIS) {
      throw exception;
    }
    retries++;
    try {
      Thread.sleep(BACKOFF_MILLIS);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      throw exception;
    }
  }

  @Override
  // Feign requires clone() to create a fresh retry budget for each request, not copy the current state.
  @SuppressWarnings({"java:S2975", "java:S1182"})
  public Retryer clone() {
    return new TransportCompanyRetryer();
  }

}
