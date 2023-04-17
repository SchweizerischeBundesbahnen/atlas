package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.TimetableHearingStatement.Fields;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class NoValidVersionAtDateException extends AtlasException {

  private final LocalDate validAt;
  private final String swissId;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message(getInfoMessage())
        .error(getInfoMessage())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(getInfoMessage())
        .field(Fields.ttfnid)
        .displayInfo(builder()
            .code("TTH.NOTIFICATION.NO_VERSION_VALID_AT")
            .with("swissId", swissId)
            .with("validAt", validAt)
            .build())
        .build());
  }

  public String getInfoMessage() {
    return "There is no version of " + swissId + " valid at " + validAt.format(
        DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH));
  }
}
