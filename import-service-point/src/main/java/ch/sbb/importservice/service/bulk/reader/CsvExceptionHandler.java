package ch.sbb.importservice.service.bulk.reader;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.importservice.service.bulk.reader.DataMappingError.ExpectedType;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Is here to collect all the errors in one csv line.
 * It returns a dummy value so jackson continues the deserialization process.
 * This allows us to continue after the first exception.
 * Errors are aggregated in errors and have to be queued after deserialization.
 */
@Getter
public class CsvExceptionHandler extends DeserializationProblemHandler {

  private final List<DataMappingError> errors = new ArrayList<>();

  public List<BulkImportError> getDataMappingErrors() {
    return errors.stream().map(DataMappingError::toBulkImportError).toList();
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
