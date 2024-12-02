package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublineTypeMissmatchException extends AtlasException {

  private static final String CODE = "LIDI.SUBLINE.TYPE.MISSMATCH";

  private final SublineType sublineType;
  private final LineType lineType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("SublineType does not correspont to LineType")
        .error("SublineType does not correspont to LineType")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.sublineType)
        .message("SublineType {0} does not match LineType {1}")
        .displayInfo(builder()
            .code(CODE)
            .with(Fields.sublineType, sublineType.name())
            .with("lineType", lineType.name())
            .build()).build());
  }

}
