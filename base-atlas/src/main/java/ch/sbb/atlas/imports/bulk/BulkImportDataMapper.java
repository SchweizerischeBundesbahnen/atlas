package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.annotation.DefaultMapping;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

public abstract class BulkImportDataMapper {

  protected <T, V> void applyDefaultMapping(T update, V targetModel) {
    applyDefaultMapping(update, null, targetModel);
  }

  @SneakyThrows
  protected <T, U, V> void applyDefaultMapping(T update, U currentEntity, V targetModel) {
    for (Field updateField : update.getClass().getDeclaredFields()) {
      if (updateField.isAnnotationPresent(DefaultMapping.class)) {
        ReflectionUtils.makeAccessible(updateField);

        Field targetField = ReflectionUtils.findField(targetModel.getClass(), updateField.getName());
        if (Objects.isNull(targetField)) {
          throw new NoSuchFieldException("Not found following field for default mapping application: " + updateField.getName());
        }
        ReflectionUtils.makeAccessible(targetField);

        Object updateValue = ReflectionUtils.getField(updateField, update);
        if (updateValue != null) {
          setFieldValue(targetField, targetModel, updateValue);
        } else if (Objects.nonNull(currentEntity)) {
          Field defaultField = ReflectionUtils.findField(currentEntity.getClass(), updateField.getName());
          if (Objects.isNull(defaultField)) {
            throw new NoSuchFieldException("Not found following field for default mapping application: " + updateField.getName());
          }
          ReflectionUtils.makeAccessible(defaultField);
          Object defaultValue = ReflectionUtils.getField(defaultField, currentEntity);
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
