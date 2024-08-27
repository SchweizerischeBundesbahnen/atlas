package ch.sbb.importservice.service.bulk.log;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogFile {

  private final List<LogEntry> logEntries = new ArrayList<>();

  public LogFile appendErrors(BulkImportUpdateContainer<?> container) {
    if (container.hasErrors()) {
      List<BulkImportError> errors = container.getDataValidationErrors().stream()
          .map(dataValidationError -> BulkImportError.builder()
              .errorMessage(
                  "Expected " + dataValidationError.getExpectedType() + " but got " + dataValidationError.getErrorValue()
                      + " in column " + dataValidationError.getField())
              .displayInfo(DisplayInfo.builder()
                  .code("BULK_IMPORT.VALIDATION.DATA_VALIDATION_ERROR")
                  .with("field", dataValidationError.getField())
                  .with("errorValue", dataValidationError.getErrorValue())
                  .with("expectedType", dataValidationError.getExpectedType().toString())
                  .build())
              .build())
          .toList();
      this.getLogEntries().add(LogEntry.builder()
          .lineNumber(container.getLineNumber())
          .status(BulkImportStatus.DATA_VALIDATION_ERROR)
          .errors(errors)
          .build());
    }
    return this;
  }

  @Data
  @Builder
  public static class LogEntry {

    private final int lineNumber;
    private final BulkImportStatus status;
    @Builder.Default
    private final List<BulkImportError> errors = new ArrayList<>();

  }

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
