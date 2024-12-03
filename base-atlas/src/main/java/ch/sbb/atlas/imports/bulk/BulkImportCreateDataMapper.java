package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.annotation.DefaultMapping;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class BulkImportCreateDataMapper<T, V> {

  public V applyCreate(BulkImportUpdateContainer<T> container, V targetModel) {
    applyDefaultMapping(container.getObject(), targetModel);
    applySpecificCreate(container.getObject(), targetModel);

    return targetModel;
  }

  protected void applySpecificCreate(T update, V targetModel) {
    // Override if needed
  }

  private void applyDefaultMapping(T update, V targetModel) {
    for (Field updateField : update.getClass().getDeclaredFields()) {
      if (updateField.isAnnotationPresent(DefaultMapping.class)) {
        ReflectionUtils.makeAccessible(Objects.requireNonNull(updateField));

        Field targetField = ReflectionUtils.findField(targetModel.getClass(), updateField.getName());
        ReflectionUtils.makeAccessible(Objects.requireNonNull(targetField));

        Object updateValue = ReflectionUtils.getField(updateField, update);
        setFieldValue(targetField, targetModel, updateValue);
      }
    }
  }

  private void setFieldValue(Field targetField, V targetModel, Object defaultValue) {
    if (targetField.getType() == List.class && defaultValue instanceof Collection) {
      ReflectionUtils.setField(targetField, targetModel, new ArrayList<>((Collection<?>) defaultValue));
    } else {
      ReflectionUtils.setField(targetField, targetModel, defaultValue);
    }
  }
}
