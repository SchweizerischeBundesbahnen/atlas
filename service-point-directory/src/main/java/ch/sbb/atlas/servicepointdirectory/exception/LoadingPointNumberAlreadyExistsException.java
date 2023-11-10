package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.servicepoint.LoadingPointVersionModel.Fields;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class LoadingPointNumberAlreadyExistsException extends AtlasException {

  private final ServicePointNumber servicePointNumber;
  private final Integer number;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("The loading point with number " + number + " already exists for service point with number "
            + servicePointNumber.getNumber())
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
        .field(Fields.number)
        .message("LoadingPoint with number {0} already exists for service point with number {1}.")
        .displayInfo(ErrorResponse.DisplayInfo.builder()
            .code("SEPODI.LOADING_POINTS.CONFLICT.NUMBER")
            .with("number", String.valueOf(number))
            .with("servicePointNumber", String.valueOf(servicePointNumber.getNumber()))
            .build())
        .build();
    SortedSet<Detail> details = new TreeSet<>();
    details.add(detail);
    return details;
  }
}
