package ch.sbb.atlas.timetable.hearing.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear.Fields;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class HearingCurrentlyActiveException extends AtlasException {

  private static final String ALREADY_AN_ACTIVE_HEARING = "There is already an active hearing";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message(ALREADY_AN_ACTIVE_HEARING)
        .error(ALREADY_AN_ACTIVE_HEARING)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(ALREADY_AN_ACTIVE_HEARING)
        .field(Fields.hearingStatus)
        .displayInfo(builder().code("HEARING.ERROR.ALREADY_AN_ACTIVE_HEARING").build())
        .build());
  }
}
