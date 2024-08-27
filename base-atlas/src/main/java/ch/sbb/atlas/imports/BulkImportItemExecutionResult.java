package ch.sbb.atlas.imports;

import ch.sbb.atlas.api.model.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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

  public boolean isSuccess() {
    return errorResponse == null;
  }
}
