package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublineOutsideOfLineRangeException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.PRECONDITION.";
  private static final String ERROR = "Subline outside of the line range";

  private final SublineVersion newVersion;
  private final String swissLineNumber;
  private final LocalDate lineValidFrom;
  private final LocalDate lineValidTo;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .status(HttpStatus.PRECONDITION_FAILED.value())
                        .message("A precondition fail occurred due to a business rule")
                        .error(ERROR)
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
                          .field(Fields.mainlineSlnid)
                          .message(
                              "The subline range {0}-{1} is outside of the line {2} range {3}-{4}")
                          .displayInfo(builder()
                              .code(CODE_PREFIX + "SUBLINE_OUTSIDE_OF_LINE_RANGE")
                              .with(SublineVersion.Fields.validFrom, newVersion.getValidFrom())
                              .with(SublineVersion.Fields.validTo, newVersion.getValidTo())
                              .with("mainline.swissLineNumber", swissLineNumber)
                              .with("mainline.validFrom", lineValidFrom)
                              .with("mainline.validTo", lineValidTo)
                              .build())
                          .build();
    return List.of(detail);
  }

}