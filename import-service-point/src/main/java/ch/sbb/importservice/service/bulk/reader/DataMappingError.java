package ch.sbb.importservice.service.bulk.reader;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataMappingError {

  private final String field;
  private final String errorValue;
  private final ExpectedType expectedType;

  public enum ExpectedType {
    INTEGER,
    DATE,
    ENUM,
    DOUBLE,
    BOOLEAN,
  }

  public BulkImportError toBulkImportError() {
    return BulkImportError.builder()
        .errorMessage(
            "Expected " + getExpectedType() + " but got " + getErrorValue()
                + " in column " + getField())
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.DATA_MAPPING_ERROR")
            .with("field", getField())
            .with("errorValue", getErrorValue())
            .with("expectedType", getExpectedType().toString())
            .build())
        .build();
  }

}