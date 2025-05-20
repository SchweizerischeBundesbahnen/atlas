package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class InvalidFareStopException extends AtlasException {

  private static final String ERROR_MESSAGE = "FareStop requires to belong to ASP and is not allowed to have a geolocation";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(ERROR_MESSAGE)
        .error(ERROR_MESSAGE)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(ERROR_MESSAGE)
        .displayInfo(builder()
            .code("SEPODI.SERVICE_POINTS.ERROR.INVALID_FARE_STOP")
            .build())
        .build());
  }

}