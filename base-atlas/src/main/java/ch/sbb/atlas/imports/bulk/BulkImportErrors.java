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
                .errorMessage("The Height must be below 100'000.")
                .displayInfo(DisplayInfo.builder()
                        .code("BULK_IMPORT.VALIDATION.HEIGHT_TO_HIGH")
                        .with("field", field)
                        .build()).build();
    }

    public static BulkImportError invalidGeography(String field) {
        return BulkImportError.builder()
                .errorMessage("Invalid Geography")
                .displayInfo(DisplayInfo.builder()
                        .code("BULK_IMPORT.VALIDATION.INVALID_GEOGRAPHY")
                        .with("field", field)
                        .build()).build();
    }

    public static BulkImportError isMeansOfTransportMissing(String field) {
        return BulkImportError.builder()
                .errorMessage("MeansOfTransport is missing or StopPointType is missing.")
                .displayInfo(DisplayInfo.builder()
                        .code("BULK_IMPORT.VALIDATION.MEANS_OF_TRANSPORT_IS_MISSING")
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
