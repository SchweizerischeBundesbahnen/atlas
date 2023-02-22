package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SubLineAssignToLineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.CONFLICT.";
  private static final String ERROR = "Subline conflict";

  private final SublineVersion actualSubline;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    Detail detail = Detail.builder()
        .field(Fields.mainlineSlnid)
        .message("The mainline {0} cannot be changed")
        .displayInfo(builder()
            .code(CODE_PREFIX + "ASSIGN_DIFFERENT_LINE_CONFLICT")
            .with(Fields.mainlineSlnid, actualSubline.getMainlineSlnid())
            .build())
        .build();
    return List.of(detail);
  }

}
