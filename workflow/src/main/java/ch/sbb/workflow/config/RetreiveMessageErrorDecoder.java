package ch.sbb.workflow.config;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.workflow.exception.AtlasClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class RetreiveMessageErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String s, Response response) {
    ErrorResponse exceptionMessage;
    try (InputStream responseBodyIs = response.body().asInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      exceptionMessage = mapper.readValue(responseBodyIs, ErrorResponse.class);
      return new AtlasClientException(exceptionMessage);
    } catch (IOException e) {
      log.error("Error during decoding: {}", e.getMessage(), e);
      return new AtlasClientException(getUnexpectedError(response));
    }
  }

  private ErrorResponse getUnexpectedError(Response response) {
    return ErrorResponse.builder()
        .status(response.status())
        .message(response.reason())
        .error("atlas Client2Client: Communication error with other service")
        .build();
  }

}
