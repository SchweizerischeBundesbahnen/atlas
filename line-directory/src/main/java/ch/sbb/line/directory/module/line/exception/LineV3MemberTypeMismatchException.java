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

@RequiredArgsConstructor
public class LineV3MemberTypeMismatchException extends AtlasException {

  private static final String CODE = "LIDI.SUBLINE.TYPE.MISSMATCH";
  private static final String MESSAGE = "LineType {0} does not match LineType {1} of the referenced line";

  private final LineType memberType;
  private final LineType referencedLineType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("LineType does not correspond to LineType of the referenced line")
        .error("LineType does not correspond to LineType of the referenced line")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.lineType)
        .message(MESSAGE)
        .displayInfo(builder()
            .code(CODE)
            .with(Fields.lineType, memberType.name())
            .with("referencedLineType", referencedLineType.name())
            .build()).build());
  }

}
