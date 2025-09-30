package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ServicePointStatusRevokedChangeNotAllowedException extends AtlasException {

  private transient final ServicePointNumber servicePointNumber;
  private final Status servicePointStatus;
  private static final String ERROR_MESSAGE = "ServicePoint Status cannot be changed for Status REVOKED and can be updated only"
      + " from DRAFT to VALIDATED!";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(ERROR_MESSAGE)
        .error("Trying to update status for ServicePointNumber " + servicePointNumber.getNumber() + " and current status: "
            + servicePointStatus)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(ERROR_MESSAGE)
        .displayInfo(builder()
            .code("SEPODI.SERVICE_POINTS.ERROR.STATUS_CHANGE_NOT_POSSIBLE")
            .build())
        .build());
  }

}