package ch.sbb.atlas.imports;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "BulkImportItemExecutionResult")
public class BulkImportItemExecutionResult {

  private int lineNumber;

  private ErrorResponse errorResponse;

  @JsonIgnore
  public boolean isSuccess() {
    return errorResponse == null;
  }

  @JsonIgnore
  public List<BulkImportError> getErrors() {
    return getErrorResponseDetails().stream()
        .map(dataExecutionError -> BulkImportError.builder()
            .errorMessage(dataExecutionError.getMessage())
            .displayInfo(dataExecutionError.getDisplayInfo())
            .build())
        .toList();
  }

  @JsonIgnore
  private SortedSet<Detail> getErrorResponseDetails() {
    if (errorResponse == null || errorResponse.getDetails() == null) {
      return new TreeSet<>();
    } else {
      return errorResponse.getDetails();
    }
  }
}
