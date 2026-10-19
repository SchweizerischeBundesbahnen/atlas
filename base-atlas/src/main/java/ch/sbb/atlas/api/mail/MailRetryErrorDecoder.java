package ch.sbb.atlas.api.mail;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

class MailRetryErrorDecoder implements ErrorDecoder {

  private static final int REQUEST_TIMEOUT = 408;
  private static final int TOO_MANY_REQUESTS = 429;
  private static final int SERVER_ERROR = 500;

  @Override
  public Exception decode(String methodKey, Response response) {
    FeignException exception = FeignException.errorStatus(methodKey, response);
    if (!isRetryable(response.status())) {
      return exception;
    }
    Request request = response.request();
    return new RetryableException(
        response.status(),
        exception.getMessage(),
        request.httpMethod(),
        exception,
        (Long) null,
        request
    );
  }

  private boolean isRetryable(int status) {
    return status == REQUEST_TIMEOUT || status == TOO_MANY_REQUESTS || status >= SERVER_ERROR;
  }

}
