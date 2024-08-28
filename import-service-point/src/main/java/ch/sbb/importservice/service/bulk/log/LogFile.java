package ch.sbb.importservice.service.bulk.log;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogFile {

  @Builder.Default
  private List<LogEntry> logEntries = new ArrayList<>();

  public static LogEntry mapToDataValidationLogEntry(BulkImportUpdateContainer<?> container) {
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
    return LogEntry.builder()
        .lineNumber(container.getLineNumber())
        .status(BulkImportStatus.DATA_VALIDATION_ERROR)
        .errors(errors)
        .build();
  }

  public static LogEntry mapToDataExecutionLogEntry(BulkImportUpdateContainer<?> container) {
    List<BulkImportError> errors = container.getDataExecutionErrors().stream()
        .map(ErrorResponse::getDetails)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(dataExecutionError -> BulkImportError.builder()
            .errorMessage(dataExecutionError.getMessage())
            .displayInfo(dataExecutionError.getDisplayInfo())
            .build())
        .toList();
    return LogEntry.builder()
        .lineNumber(container.getLineNumber())
        .status(container.hasDataExecutionErrors() ? BulkImportStatus.DATA_EXECUTION_ERROR : BulkImportStatus.SUCCESS)
        .errors(errors)
        .build();
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
