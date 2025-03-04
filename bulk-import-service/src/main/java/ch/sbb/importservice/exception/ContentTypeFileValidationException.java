package ch.sbb.importservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ContentTypeFileValidationException extends AtlasException {

  private final String contentType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("""
            Import File validation failed.
            """)
        .error("ContentType " + contentType + " not supported")
        .details(new TreeSet<>())
        .build();
  }

}
