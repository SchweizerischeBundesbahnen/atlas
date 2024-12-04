package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BulkImportUpdateDataMapper<T, U, V> extends BulkImportDataMapper {

    public V applyUpdate(BulkImportUpdateContainer<T> container, U currentEntity, V targetModel) {
        applyDefaultMapping(container.getObject(), currentEntity, targetModel);
        applyCopyFromCurrentVersion(container.getObject(), currentEntity, targetModel);
        applySpecificUpdate(container.getObject(), currentEntity, targetModel);

        applyNulling(container, targetModel);
        return targetModel;
    }

    protected void applySpecificUpdate(T update, U currentEntity, V targetModel) {
        // Override if needed
    }

    private void applyNulling(BulkImportUpdateContainer<T> container, V targetModel) {
        List<String> attributesToNull = container.getAttributesToNull();
        for (String attributeToNull : attributesToNull) {
            Field fieldToNull = ReflectionUtils.findField(container.getObject().getClass(), attributeToNull);
            if (fieldToNull == null || !fieldToNull.isAnnotationPresent(Nulling.class)) {
                throw new AttributeNullingNotSupportedException(attributeToNull);
            }

            String propertyToNull = fieldToNull.getAnnotation(Nulling.class).property();
            String pathToNull = StringUtils.isBlank(propertyToNull) ? fieldToNull.getName() : propertyToNull;
            ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(targetModel);
            propertyAccessor.setAutoGrowNestedPaths(true);
            propertyAccessor.setPropertyValue(pathToNull, null);
        }
    }

    private void applyCopyFromCurrentVersion(T update, U currentEntity, V targetModel) {
        if (update.getClass().isAnnotationPresent(CopyFromCurrentVersion.class)) {
            CopyFromCurrentVersion copyFromCurrentVersion = update.getClass().getAnnotation(CopyFromCurrentVersion.class);
            Map<String, String> mappings = Stream.of(copyFromCurrentVersion.value())
                    .collect(Collectors.toMap(Mapping::current, Mapping::target));

            mappings.forEach((current, target) -> {
                ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(currentEntity);
                if (propertyAccessor.isReadableProperty(current)) {
                    Object currentValue = propertyAccessor.getPropertyValue(current);

                    Field targetField = ReflectionUtils.findField(targetModel.getClass(), target);
                    ReflectionUtils.makeAccessible(Objects.requireNonNull(targetField));
                    setFieldValue(targetField, targetModel, currentValue);
                }
            });
        }
    }

}
