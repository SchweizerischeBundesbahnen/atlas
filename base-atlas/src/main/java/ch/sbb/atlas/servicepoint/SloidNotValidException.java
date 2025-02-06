package ch.sbb.atlas.servicepoint;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class SloidNotValidException extends AtlasException {

  private final String sloid;
  private final String reason;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("The SLOID " + sloid + " is not valid due to: " + reason)
        .error("SLOID not valid")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("sloid")
        .message("The SLOID " + sloid + " is not valid due to: " + reason)
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SLOID_INVALID")
            .build())
        .build());
    return errorDetails;
  }
}
