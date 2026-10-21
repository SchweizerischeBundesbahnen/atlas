package ch.sbb.atlas.api.client.bodi;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

class TransportCompanyRetryErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    Request request = response.request();
    if (!TransportCompanyRetryer.isRetryableRequest(request) || !isRetryable(response.status())) {
      return FeignException.errorStatus(methodKey, response);
    }

    Exception exception = defaultDecoder.decode(methodKey, response);
    if (exception instanceof RetryableException || response.status() == 429) {
      return exception;
    }
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
    return status == 429 || status == 502 || status == 503 || status == 504;
  }

}
