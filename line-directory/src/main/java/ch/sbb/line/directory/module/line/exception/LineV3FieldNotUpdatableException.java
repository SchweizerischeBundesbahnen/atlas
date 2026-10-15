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
public class LineV3FieldNotUpdatableException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.LINE.CONFLICT.NON_UPDATABLE";
  private static final String ERROR = "Line field non-updatable";

  private final String newValue;
  private final String fieldName;
  private final LineType lineType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("Try to update line field not updatable")
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(fieldName)
        .message("Field {0} cannot be update to {1} when LineType is {2}")
        .displayInfo(builder()
            .code(ERROR_CODE)
            .with("field", fieldName)
            .with(fieldName, newValue)
            .with(Fields.lineType, lineType.toString())
            .build()).build());
  }

}
