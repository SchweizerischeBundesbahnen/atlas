package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.lidi.LineVersionModelV2.Fields;
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

  private static final String ERROR_CODE_ORDERLY = "LIDI.LINE.ERROR.MANDATORY.ORDERLY";
  private static final String ERROR_CODE_NOT_ORDERLY = "LIDI.LINE.ERROR.MANDATORY.NOT_ORDERLY";

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
            .code(getErrorCode())
            .build()).build());
  }

  String getErrorMessage() {
    return lineType == LineType.ORDERLY ? MSG_LINE_TYPE_ORDERLY : MSG_LINE_TYPE_NOT_ORDERLY;
  }

  String getErrorCode() {
    return lineType == LineType.ORDERLY ? ERROR_CODE_ORDERLY : ERROR_CODE_NOT_ORDERLY;
  }
}
