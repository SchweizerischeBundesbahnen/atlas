package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ServicePointStatusChangeNotAllowedException extends AtlasException {

  private final Status actualServicePointStatus;
  private final Status currentServicePointStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(MessageFormat.format(
            "ServicePoint Status cannot be changed from {0} to {1}!", actualServicePointStatus, currentServicePointStatus))
        .error("Update status not allowed!")
        .build();
  }

}
