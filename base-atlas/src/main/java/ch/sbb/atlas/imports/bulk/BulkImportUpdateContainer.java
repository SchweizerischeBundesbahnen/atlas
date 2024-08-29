package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Data
public class BulkImportUpdateContainer<T> implements BulkImportContainer {

  private int lineNumber;

  @Valid
  private T object;

  @Builder.Default
  private List<String> attributesToNull = new ArrayList<>();

  private BulkImportLogEntry bulkImportLogEntry;

  public boolean hasDataValidationErrors() {
    return bulkImportLogEntry != null &&
        bulkImportLogEntry.getStatus() == BulkImportStatus.DATA_VALIDATION_ERROR;
  }

  @Data
  @Builder
  public static class DataMappingError {

    private final String field;
    private final String errorValue;
    private final ExpectedType expectedType;

  }

  public enum ExpectedType {
    INTEGER,
    DATE,
    ENUM,
    DOUBLE,
    BOOLEAN,
  }
}
