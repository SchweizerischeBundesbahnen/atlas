package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportErrors {

  public static BulkImportError sloidXorNumber() {
    return BulkImportError.builder()
        .errorMessage("SlOID xor number must might be given")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.SLOID_XOR_NUMBER")
            .build()).build();
  }

  public static BulkImportError notNull(String field) {
    return BulkImportError.builder()
        .errorMessage("Field " + field + " must not be null")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.FIELD_MANDATORY")
            .with("field", field)
            .build()).build();
  }

  public static BulkImportError invalidServicePointNumber() {
    return BulkImportError.builder()
        .errorMessage("Invalid Service Point Number")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.INVALID_SERVICE_POINT_NUMBER")
            .build()).build();
  }
}
