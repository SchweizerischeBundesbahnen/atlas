package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV2.Fields;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LineTypeOrderlyException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.LINE.ERROR.MANDATORY";
  private static final String MSG_LINE_TYPE_ORDERLY = "SwissLineNumber and ConcessionType must not be null for LineType Orderly";
  private static final String MSG_LINE_TYPE_NOT_ORDERLY = "SwissLineNumber and ConcessionType only allowed for LineType Orderly";
  private final LineType lineType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(getErrorMessage())
        .error(getErrorMessage())
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.lineType)
        .message(getErrorMessage())
        .displayInfo(builder()
            .code(ERROR_CODE)
            .build()).build());
  }

  String getErrorMessage() {
    return lineType == LineType.ORDERLY ? MSG_LINE_TYPE_ORDERLY : MSG_LINE_TYPE_NOT_ORDERLY;
  }

}
