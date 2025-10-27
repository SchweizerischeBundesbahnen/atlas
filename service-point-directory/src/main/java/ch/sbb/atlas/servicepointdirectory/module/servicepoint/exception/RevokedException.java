package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

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
public class RevokedException extends AtlasException {

  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(getErrorMessage())
        .error(getErrorMessage())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(getErrorMessage())
        .displayInfo(builder()
            .code("COMMON.REVOKED")
            .with("field", "SLOID")
            .with("value", sloid)
            .build())
        .build());
  }

  private String getErrorMessage() {
    return "Object with sloid " + sloid + " is revoked";
  }
}