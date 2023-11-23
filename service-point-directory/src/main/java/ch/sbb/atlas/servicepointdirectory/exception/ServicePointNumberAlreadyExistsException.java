package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ServicePointNumberAlreadyExistsException extends AtlasException {

  private final ServicePointNumber servicePointNumber;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("The service point with number " + servicePointNumber.getNumber() + " already exists.")
        .error("Service Point number already exists")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("number")
        .message("Service Point with number {0} is already existing.")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.NUMBER_ALREADY_USED")
            .with("number", servicePointNumber.getNumber().toString())
            .build())
        .build());
    return errorDetails;
  }

}
