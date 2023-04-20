package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ForbiddenDueToHearingYearSettings extends AtlasException {

  private final Long hearingYear;
  private final String settingField;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("Operation not allowed")
        .error("The timetable hearing year settings of: " + hearingYear + " are disabling this operation.")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("The timetable hearing year settings of: " + hearingYear + " are disabling this operation.")
        .field(settingField)
        .displayInfo(builder().code("").build()) // TODO: define
        .build());
  }

}
