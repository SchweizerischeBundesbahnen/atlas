package ch.sbb.importservice.service.bulk.reader;

import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer.DataMappingError;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer.ExpectedType;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class CsvExceptionHandler extends DeserializationProblemHandler {

  private final List<DataMappingError> errors = new ArrayList<>();

  public List<BulkImportError> getDataMappingErrors() {
    return errors.stream().map(dataMappingError -> BulkImportError.builder()
            .errorMessage(
                "Expected " + dataMappingError.getExpectedType() + " but got " + dataMappingError.getErrorValue()
                    + " in column " + dataMappingError.getField())
            .displayInfo(DisplayInfo.builder()
                .code("BULK_IMPORT.VALIDATION.DATA_MAPPING_ERROR")
                .with("field", dataMappingError.getField())
                .with("errorValue", dataMappingError.getErrorValue())
                .with("expectedType", dataMappingError.getExpectedType().toString())
                .build())
            .build())
        .toList();
  }

  @Override
  public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg)
      throws IOException {
    errors.add(DataMappingError.builder()
        .expectedType(getExpectedType(targetType))
        .field(ctxt.getParser().currentName())
        .errorValue(valueToConvert)
        .build());
    return getDummyValue(targetType);
  }

  private ExpectedType getExpectedType(Class<?> targetClass) {
    if (targetClass == Integer.class) {
      return ExpectedType.INTEGER;
    }
    if (targetClass == LocalDate.class) {
      return ExpectedType.DATE;
    }
    if (targetClass == Boolean.class) {
      return ExpectedType.BOOLEAN;
    }
    if (targetClass == Double.class) {
      return ExpectedType.DOUBLE;
    }
    if (targetClass.isEnum()) {
      return ExpectedType.ENUM;
    }
    throw new IllegalStateException();
  }

  private Object getDummyValue(Class<?> targetClass) {
    if (targetClass == Integer.class) {
      return 0;
    }
    if (targetClass == Double.class) {
      return 0.0;
    }
    if (targetClass == LocalDate.class) {
      return LocalDate.now();
    }
    if (targetClass == Boolean.class) {
      return Boolean.FALSE;
    }
    if (targetClass.isEnum()) {
      return targetClass.getEnumConstants()[0];
    }
    return DeserializationProblemHandler.NOT_HANDLED;
  }
}
