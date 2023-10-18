package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationNotAllowedException extends AtlasException {

  private final ServicePointVersion editedVersion;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("You are not allowed to terminate the stopPoint with number=" + editedVersion.getNumber().getNumber())
        .error("Termination not allowed")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("termination")
        .message("You are not allowed to terminate {0}")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SERVICE_POINT.TERMINATION_FORBIDDEN")
            .with("number", String.valueOf(editedVersion.getNumber().getNumber()))
            .build())
        .build());
    return errorDetails;
  }
}


