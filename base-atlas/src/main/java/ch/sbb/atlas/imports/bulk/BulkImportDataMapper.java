package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.annotation.DefaultMapping;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;

public abstract class BulkImportDataMapper {

  protected <T, U, V> void applyDefaultMapping(T update, U currentEntity, V targetModel) {
    applyDefaultMapping(update, Optional.of(currentEntity), targetModel);
  }

  protected <T, V> void applyDefaultMapping(T update, V targetModel) {
    applyDefaultMapping(update, Optional.empty(), targetModel);
  }

  private <T, U, V> void applyDefaultMapping(T update, Optional<U> currentEntity, V targetModel) {
    for (Field updateField : update.getClass().getDeclaredFields()) {
      if (updateField.isAnnotationPresent(DefaultMapping.class)) {
        ReflectionUtils.makeAccessible(Objects.requireNonNull(updateField));

        Field targetField = ReflectionUtils.findField(targetModel.getClass(), updateField.getName());
        ReflectionUtils.makeAccessible(Objects.requireNonNull(targetField));

        Object updateValue = ReflectionUtils.getField(updateField, update);
        if (updateValue != null) {
          setFieldValue(targetField, targetModel, updateValue);
        } else if (currentEntity.isPresent()) {
          Field defaultField = ReflectionUtils.findField(currentEntity.get().getClass(), updateField.getName());
          ReflectionUtils.makeAccessible(Objects.requireNonNull(defaultField));
          Object defaultValue = ReflectionUtils.getField(Objects.requireNonNull(defaultField), currentEntity.get());

          setFieldValue(targetField, targetModel, defaultValue);
        }
      }
    }
  }

  protected <V> void setFieldValue(Field targetField, V targetModel, Object defaultValue) {
    if (targetField.getType() == List.class && defaultValue instanceof Collection) {
      ReflectionUtils.setField(targetField, targetModel, new ArrayList<>((Collection<?>) defaultValue));
    } else {
      ReflectionUtils.setField(targetField, targetModel, defaultValue);
    }
  }
}
