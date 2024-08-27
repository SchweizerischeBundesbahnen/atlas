package ch.sbb.atlas.imports.bulk;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
@Data
public class BulkImportUpdateContainer<T> implements BulkImportContainer {

  private final int lineNumber;

  @Valid
  private final T object;

  @Builder.Default
  private final List<String> attributesToNull = new ArrayList<>();

  @Builder.Default
  private final List<DataValidationError> dataValidationErrors = new ArrayList<>();

  public boolean hasErrors() {
    return !dataValidationErrors.isEmpty();
  }

  @Data
  @Builder
  public static class DataValidationError {

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
