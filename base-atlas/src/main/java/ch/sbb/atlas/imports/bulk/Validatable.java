package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface Validatable<T> {

  @JsonIgnore
  List<BulkImportError> validate();

  @JsonIgnore
  List<UniqueField<T>> uniqueFields();

  @Getter
  @RequiredArgsConstructor
  class UniqueField<T> {

    private final String field;
    private final Function<T, Object> fieldValueExtractor;
  }
}
