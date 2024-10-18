package ch.sbb.atlas.imports.util;

import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportUtility {

  public static <T> void applyUpdateIfValueNotNull(T value, Consumer<T> setterFunction) {
    if (value != null) {
      setterFunction.accept(value);
    }
  }

}
