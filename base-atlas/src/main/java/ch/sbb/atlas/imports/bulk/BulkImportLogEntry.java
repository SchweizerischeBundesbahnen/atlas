package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkImportLogEntry {

  private final int lineNumber;
  private final BulkImportStatus status;
  @Builder.Default
  private final List<BulkImportError> errors = new ArrayList<>();

  @Data
  @Builder
  public static class BulkImportError {

    private final String errorMessage;
    private final DisplayInfo displayInfo;

  }

  public enum BulkImportStatus {
    SUCCESS,
    DATA_VALIDATION_ERROR,
    DATA_EXECUTION_ERROR,
  }
}