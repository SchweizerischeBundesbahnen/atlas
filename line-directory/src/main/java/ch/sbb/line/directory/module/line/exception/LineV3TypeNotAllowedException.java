package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * LineType TECHNICAL and CONCESSION are only allowed for members of a line (referencedLineSlnid set).
 */
@RequiredArgsConstructor
public class LineV3TypeNotAllowedException extends AtlasException {

  private static final String CODE = "LIDI.LINE.ERROR.TYPE_NOT_ALLOWED";
  private static final String MESSAGE = "LineType {0} is only allowed for lines with a referencedLineSlnid";

  private final LineType lineType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("LineType " + lineType + " is only allowed for lines with a referencedLineSlnid")
        .error("LineType not allowed for a line without referencedLineSlnid")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.lineType)
        .message(MESSAGE)
        .displayInfo(builder()
            .code(CODE)
            .with(Fields.lineType, lineType.name())
            .build()).build());
  }

}
