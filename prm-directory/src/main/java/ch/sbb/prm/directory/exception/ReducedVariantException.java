package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper.COMPLETE_MEANS_OF_TRANSPORT;

@RequiredArgsConstructor
@Getter
public class ReducedVariantException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("Object creation not allowed for reduced variant!")
        .error("Only StopPoints that contain only complete mean of transports variant [" + COMPLETE_MEANS_OF_TRANSPORT
            + "] are allowed to create this object.")
        .build();
  }
}
