package ch.sbb.atlas.imports.bulk;

import java.util.function.Consumer;

public abstract class BulkImportDataMapper {

  protected static <T> void applyValueWithDefault(T value, T defaultValue, Consumer<T> setterFunction) {
    if (value != null) {
      setterFunction.accept(value);
    } else {
      setterFunction.accept(defaultValue);
    }
  }

}
