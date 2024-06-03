package ch.sbb.workflow.client;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.workflow.exception.SePoDiClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetreiveMessageErrorDecoder implements ErrorDecoder {

  public static final String CLIENT_ERROR_MSG = "SePoDi Client Error";

  @Override
  public Exception decode(String s, Response response) {
    ErrorResponse exceptionMessage;
    try (InputStream responseBodyIs = response.body().asInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      exceptionMessage = mapper.readValue(responseBodyIs, ErrorResponse.class);
      return new SePoDiClientException(exceptionMessage);
    } catch (IOException e) {
      log.error(e.getMessage());
      return new SePoDiClientException(getUnexpectedError(response));
    }
  }

  private ErrorResponse getUnexpectedError(Response response) {
    return ErrorResponse.builder()
        .status(response.status())
        .message(response.reason())
        .error(CLIENT_ERROR_MSG)
        .build();
  }

}