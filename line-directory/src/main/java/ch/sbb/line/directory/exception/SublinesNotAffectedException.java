package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublinesNotAffectedException extends AtlasException {

  private final DateRange dateRange;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Trunction not found")
        .error("The specified date range is not a truncation. No Sublines were Affected.")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  //TODO ADD CODE & Tests
  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("The specified date range is not a truncation. No Sublines were Affected.")
        .displayInfo(builder()
            .code("")
            .with("validFrom", dateRange.getFrom())
            .with("validTo", dateRange.getTo())
            .build())
        .build());
  }
}
