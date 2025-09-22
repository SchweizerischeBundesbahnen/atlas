package ch.sbb.prm.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class BulkPlatformUpdateValidationException extends AtlasException {

  private final boolean isReduced;
  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(buildErrorMsg())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(buildErrorMsg())
        .displayInfo(builder()
            .code(buildErrorCode())
            .with("sloid", sloid)
            .build())
        .build());
  }

  private String buildErrorCode() {
    if (isReduced) {
      return "BULK_IMPORT.ERROR.PRM.BAD_REQUEST_REDUCED";
    } else {
      return "BULK_IMPORT.ERROR.PRM.BAD_REQUEST_COMPLETE";
    }
  }

  private String buildErrorMsg() {
    if (isReduced) {
      return "Attempting to save a Reduced object with wrong properties population!";
    } else {
      return "Attempting to save a Complete object with wrong properties population!";
    }
  }
}
