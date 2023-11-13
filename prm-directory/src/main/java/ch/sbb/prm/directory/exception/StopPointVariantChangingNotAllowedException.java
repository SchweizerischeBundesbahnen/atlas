package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class StopPointVariantChangingNotAllowedException extends AtlasException {

  private final StopPointVersion current;

  @Override
  public ErrorResponse getErrorResponse() {
    buildErrorMessage();

    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(buildErrorMessage())
        .build();
  }

  private String buildErrorMessage() {
    if (current.isReduced()) {
      return "Changing from Reduced to Complete not allowed! Allowed means of transport: "
          + PrmMeansOfTransportHelper.REDUCED_MEANS_OF_TRANSPORT.toString();
    }
    return
        "Changing from Complete to Reduced not allowed! Allowed means of transport: "
            + PrmMeansOfTransportHelper.COMPLETE_MEANS_OF_TRANSPORT.toString();
  }

}
