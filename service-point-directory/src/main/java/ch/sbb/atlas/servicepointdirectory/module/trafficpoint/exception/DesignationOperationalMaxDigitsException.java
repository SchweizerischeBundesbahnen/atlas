package ch.sbb.atlas.servicepointdirectory.module.trafficpoint.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class DesignationOperationalMaxDigitsException extends AtlasException {

  public static final String ERROR_CODE = "SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL_MAX_DIGITS_ERROR";

  private static final String FIELD = "designationOperational";

  private final transient ServicePointNumber servicePointNumber;
  private final String designationOperational;
  private final int maxDigits;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("designationOperational " + designationOperational + " exceeds the maximum of " + maxDigits
            + " digits allowed for service point " + servicePointNumber.asString()
            + " without means of transport TRAIN.")
        .error("Designation operational constraint violation")
        .details(getDetails())
        .build();
  }

  private SortedSet<Detail> getDetails() {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(Detail.builder()
        .field(FIELD)
        .message("designationOperational allows at most " + maxDigits
            + " digits for service points without means of transport TRAIN.")
        .displayInfo(DisplayInfo.builder()
            .code(ERROR_CODE)
            .with("field", FIELD)
            .with("maxDigits", String.valueOf(maxDigits))
            .build())
        .build());
    return details;
  }

}
