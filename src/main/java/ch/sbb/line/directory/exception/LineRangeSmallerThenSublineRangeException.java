package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LineRangeSmallerThenSublineRangeException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.PRECONDITION.";

  private final LineVersion newVersion;
  private final String swissSubLineNumber;
  private final LocalDate sublineVersionValidFrom;
  private final LocalDate sublineVersionValidTo;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .httpStatus(HttpStatus.PRECONDITION_FAILED.value())
                        .message("A precondition fail occurred due to a business rule")
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
                          .field(Fields.mainlineSlnid)
                          .message(
                              "The line range {0}-{1} is outside of the subline {2} range {3}-{4}")
                          .displayInfo(builder()
                              .code(CODE_PREFIX + "LINE_OUTSIDE_OF_LINE_RANGE")
                              .with(LineVersion.Fields.validFrom, newVersion.getValidFrom())
                              .with(LineVersion.Fields.validTo, newVersion.getValidTo())
                              .with(Fields.swissSublineNumber, swissSubLineNumber)
                              .with(Fields.validFrom, sublineVersionValidFrom)
                              .with(Fields.validTo, sublineVersionValidTo)
                              .build())
                          .build();
    return List.of(detail);
  }

}