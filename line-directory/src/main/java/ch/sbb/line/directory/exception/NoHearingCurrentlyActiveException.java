package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.TimetableHearingYear.Fields;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class NoHearingCurrentlyActiveException extends AtlasException {

  private static final String NO_ACTIVE_HEARING_YEAR = "There is no active hearing year";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message(NO_ACTIVE_HEARING_YEAR)
        .error(NO_ACTIVE_HEARING_YEAR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(NO_ACTIVE_HEARING_YEAR)
        .field(Fields.hearingStatus)
        .displayInfo(builder().code("HEARING.ERROR.NO_ACTIVE_HEARING").build())
        .build());
  }
}
