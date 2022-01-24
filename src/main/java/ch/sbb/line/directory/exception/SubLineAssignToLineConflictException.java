package ch.sbb.line.directory.exception;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.api.ErrorResponse.DisplayInfo;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SubLineAssignToLineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.CONFLICT.";

  private final SublineVersion newVersion;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .httpStatus(HttpStatus.CONFLICT.value())
                        .message("A conflict occurred due to a business rule")
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
                         .field(Fields.mainlineSlnid)
                         .message("The mainline {0} cannot be changed")
                         .displayInfo(DisplayInfo.builder()
                                                 .code(CODE_PREFIX + "ASSIGN_DIFFERENT_LINE_CONFLICT")
                                                 .with(Fields.mainlineSlnid,
                                                     newVersion.getMainlineSlnid())
                                                 .build())
                         .build();
    return List.of(detail);
  }

}