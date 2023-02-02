package ch.sbb.atlas.base.service.versioning.annotation;

import ch.sbb.atlas.base.service.versioning.convert.ReflectionHelper;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty.RelationType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AtlasAnnotationProcessor {

  public List<VersionableProperty> getVersionableProperties(Object object) {
    checkIfIsAtlasVersionable(object);
    return getAnnotatedField(object);
  }

  private void checkIfIsAtlasVersionable(Object object) {
    if (Objects.isNull(object)) {
      throw new AtlasVersionableException("Can't versioning a null object.");
    }

    Class<?> clazz = object.getClass();
    if (!clazz.isAnnotationPresent(AtlasVersionable.class)) {
      throw new AtlasVersionableException(
          "The class " + clazz.getSimpleName()
              + " is not annotated with @AtlasVersionable. Please check the documentation.");
    }
    if (!Versionable.class.isAssignableFrom(clazz)) {
      throw new AtlasVersionableException(
          "The class " + clazz.getSimpleName()
              + " must implement the interface Versionable. Please check the documentation.");
    }
  }

  private List<VersionableProperty> getAnnotatedField(Object object)
      throws IllegalArgumentException {
    List<VersionableProperty> versionableProperties = new ArrayList<>();
    Class<?> clazz = object.getClass();
    for (Field field : ReflectionHelper.getAllFieldsAccessible(clazz)) {
      if (field.isAnnotationPresent(AtlasVersionableProperty.class)) {
        VersionableProperty versionableProperty =
            VersionableProperty.builder()
                .fieldName(getKey(field))
                .ignoreDiff(getIgnoredDiff(field))
                .doNotOverride(getDoNotOverride(field))
                .relationsFields(Arrays.asList(getRelationFields(field)))
                .relationType(getRelationType(field))
                .build();
        versionableProperties.add(versionableProperty);
      }
    }
    if (versionableProperties.isEmpty()) {
      throw new AtlasVersionableException(
          "To versioning an Object you have to mark some properties with @AtlasVersionableProperty. Please check the "
              + "documentation.");
    }
    return versionableProperties;
  }

  private String getKey(Field field) {
    String value = field.getAnnotation(AtlasVersionableProperty.class)
        .key();
    return value.isEmpty() ? field.getName() : value;
  }

  private boolean getIgnoredDiff(Field field) {
    return field.getAnnotation(AtlasVersionableProperty.class)
        .ignoreDiff();
  }

  private boolean getDoNotOverride(Field field) {
    return field.getAnnotation(AtlasVersionableProperty.class)
        .doNotOverride();
  }

  private RelationType getRelationType(Field field) {
    return field.getAnnotation(AtlasVersionableProperty.class)
        .relationType();
  }

  private String[] getRelationFields(Field field) {
    return field.getAnnotation(AtlasVersionableProperty.class)
        .relationsFields();
  }

}
