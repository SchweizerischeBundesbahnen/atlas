package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SectorValidityException extends AtlasException {

  private final DateRange validityTrafficPoint;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Business rule validation failed")
        .error("Validity is not in range of validity of traffic point")
        .details(getDetails())
        .build();
  }

  private SortedSet<Detail> getDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("validity")
        .message("Traffic point validity from {0} to {1} violated.")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SECTORS.TRAFFIC_POINT_VALIDITY_ERROR")
            .with("from", validityTrafficPoint.getFrom())
            .with("to", validityTrafficPoint.getTo())
            .build())
        .build());
    return errorDetails;
  }
}
