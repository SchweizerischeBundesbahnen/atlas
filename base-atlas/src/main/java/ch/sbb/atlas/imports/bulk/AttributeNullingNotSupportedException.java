package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class AttributeNullingNotSupportedException extends AtlasException {

  private final String field;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Setting attribute " + field + " to null is not supported")
        .error("Attribute nulling not supported")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    return new TreeSet<>(
        List.of(
            Detail.builder()
                .field(field)
                .message("Setting attribute {0} to null is not supported")
                .displayInfo(DisplayInfo.builder()
                    .code("BULK_IMPORT.ERROR.ATTRIBUTE_NULLING_NOT_SUPPORTED")
                    .with("field", field)
                    .build())
                .build()
        )
    );
  }
}
