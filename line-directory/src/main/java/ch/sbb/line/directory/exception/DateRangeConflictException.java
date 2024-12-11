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
public class DateRangeConflictException extends AtlasException {

  private final DateRange dateRange;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("Operation not allowed")
        .error("The provided date range is invalid and outside of the parent line validity.")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("The provided date range is invalid and outside of the parent line validity.")
        .displayInfo(builder()
            .code("LIDI.LINE.CONFLICT.SUBLINE_VALIDITY")
            .with("validFrom", dateRange.getFrom())
            .with("validTo", dateRange.getTo())
            .build())
        .build());
  }
}
