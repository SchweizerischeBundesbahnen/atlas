package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportErrors {

  public static BulkImportError sloidXorNumber() {
    return BulkImportError.builder()
        .errorMessage("SlOID xor number must be given")
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

  public static BulkImportError isUicCountryCodeValid(String field) {
    return BulkImportError.builder()
        .errorMessage("The uicCountryCode is not valid.")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.COUNTRY_CODE_NOT_VALID")
            .with("field", field)
            .build()).build();
  }

  public static BulkImportError invalidNumberShort(String field) {
    return BulkImportError.builder()
        .errorMessage("The number short is not valid. Must be between 1 and 99999")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.NUMBER_SHORT_NOT_VALID")
            .with("field", field)
            .build()).build();
  }

  public static BulkImportError invalidHeight(String field) {
    return BulkImportError.builder()
        .errorMessage("A field with precision 10, scale 5 must round to an absolute value less than 10^5.")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.HEIGHT_TO_HIGH")
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

  public static BulkImportError duplicatedValue(String fieldName, String duplicatedValue) {
    return BulkImportError.builder()
        .errorMessage(fieldName + " with value " + duplicatedValue + " occurred more than once")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.VALIDATION.DUPLICATE_FIELD")
            .with("field", fieldName)
            .with("value", duplicatedValue)
            .build())
        .build();
  }
}
