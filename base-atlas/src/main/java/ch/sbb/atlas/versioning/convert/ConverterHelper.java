package ch.sbb.atlas.versioning.convert;

import static ch.sbb.atlas.versioning.model.VersionableProperty.RelationType.NONE;
import static ch.sbb.atlas.versioning.model.VersionableProperty.RelationType.ONE_TO_MANY;
import static ch.sbb.atlas.versioning.model.VersionableProperty.RelationType.ONE_TO_ONE;

import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Entity.EntityBuilder;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Property.PropertyBuilder;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConverterHelper {

  private ConverterHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static Entity convertToEditedEntity(boolean deletePropertyWhenNull,
      Versionable currentVersion, Versionable editedVersion,
      List<VersionableProperty> versionableProperties) {

    List<Property> editedProperties = extractProperties(versionableProperties, editedVersion);
    List<Property> currentProperties = extractProperties(versionableProperties, currentVersion);

    List<Property> propertiesEqualsBetweenCurrentAndEdited = new ArrayList<>();
    for (Property editedProperty : editedProperties) {
      currentProperties.stream()
          .filter(p -> p.equals(editedProperty))
          .findFirst().ifPresent(propertiesEqualsBetweenCurrentAndEdited::add);
    }

    editedProperties.removeAll(propertiesEqualsBetweenCurrentAndEdited);
    //standard case
    if (!deletePropertyWhenNull) {
      List<Property> propertiesNotEmpty = editedProperties.stream()
          .filter(Property::isNotEmpty)
          .toList();
      return buildEntity(currentVersion.getId(), propertiesNotEmpty);
    } else {
      //import case
      List<Property> propertiesWithoutDoNotOverride = editedProperties.stream().filter(property -> !property.isDoNotOverride())
          .toList();
      return buildEntity(currentVersion.getId(), propertiesWithoutDoNotOverride);
    }
  }

  public static <T extends Versionable> List<ToVersioning> convertAllObjectsToVersioning(
      List<T> currentVersions, List<VersionableProperty> versionableProperties) {
    List<ToVersioning> objectsToVersioning = new ArrayList<>();
    for (Versionable currentVersion : currentVersions) {
      objectsToVersioning.add(
          ToVersioning.builder()
              .versionable(currentVersion)
              .entity(convertToEntity(versionableProperties, currentVersion))
              .build()
      );
    }
    return objectsToVersioning;
  }

  private static <T extends Versionable> Entity convertToEntity(
      List<VersionableProperty> versionableProperties, T version) {

    List<Property> properties = extractProperties(versionableProperties, version);
    return buildEntity(version.getId(), properties);
  }

  private static <T extends Versionable> List<Property> extractProperties(
      List<VersionableProperty> versionableProperties,
      T version) {

    List<Property> properties = new ArrayList<>();
    for (VersionableProperty versionableProperty : versionableProperties) {
      if (NONE == versionableProperty.getRelationType()) {
        Property property = extractProperty(version, versionableProperty);
        properties.add(property);
      }
      if (ONE_TO_MANY == versionableProperty.getRelationType()) {
        Property extractOneToManyRelationProperty = extractOneToManyRelationProperty(
            version, versionableProperty);
        properties.add(extractOneToManyRelationProperty);
      }
      if (ONE_TO_ONE == versionableProperty.getRelationType()) {
        Property property = extractOneToOne(version, versionableProperty);
        properties.add(property);
      }
    }
    return properties;
  }

  private static <T extends Versionable> Property extractProperty(T version,
      VersionableProperty property) {
    Class<? extends Versionable> versionClass = version.getClass();
    try {
      Field declaredField = ReflectionHelper.getFieldAccessible(versionClass,
          property.getFieldName());
      Object propertyValue = declaredField.get(version);
      return buildProperty(property.getFieldName(), propertyValue, property.isIgnoreDiff(), property.isDoNotOverride());
    } catch (NoSuchFieldException | IllegalAccessException e) {
      log.error("Error during parse field {}", e.getMessage());
      throw new VersioningException("Error during parse field " + e.getMessage(), e);
    }
  }

  private static <T extends Versionable> Property extractOneToOne(T version,
      VersionableProperty property) {
    Class<? extends Versionable> versionClass = version.getClass();
    try {
      PropertyBuilder propertyBuilder = Property.builder().key(property.getFieldName());
      List<Property> relationProperties = new ArrayList<>();
      Field oneToOneRelationField = ReflectionHelper.getFieldAccessible(versionClass,
          property.getFieldName());
      Object oneToOneObject = oneToOneRelationField.get(version);
      if (oneToOneObject != null) {
        EntityBuilder entityRelationBuilder = Entity.builder();
        for (String relation : property.getRelationsFields()) {
          Field relationDeclaredField = ReflectionHelper.getFieldAccessible(
              oneToOneObject.getClass(), relation);
          Object relationField = relationDeclaredField.get(oneToOneObject);
          relationProperties.add(buildProperty(relation, relationField, property.isIgnoreDiff(), property.isDoNotOverride()));
        }
        Entity entityOneToOne = entityRelationBuilder.properties(relationProperties).build();
        return propertyBuilder.oneToOne(entityOneToOne).build();
      }
      return propertyBuilder.build();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      log.error("Error during parse field {}", e.getMessage());
      throw new VersioningException("Error during parse field " + e.getMessage(), e);
    }
  }

  private static <T extends Versionable> Property extractOneToManyRelationProperty(
      T version,
      VersionableProperty property) {

    PropertyBuilder propertyBuilder = Property.builder().key(property.getFieldName());
    List<Entity> entityRelations = new ArrayList<>();
    List<Property> relationProperties = new ArrayList<>();
    EntityBuilder entityRelationBuilder = Entity.builder();

    Class<? extends Versionable> versionClass = version.getClass();
    try {
      Field oneToManyRelationField = ReflectionHelper.getFieldAccessible(versionClass,
          property.getFieldName());
      Collection<Object> oneToManyRelationCollection = (Collection<Object>) oneToManyRelationField.get(
          version);
      if (oneToManyRelationCollection != null) {
        for (Object oneToManyRelation : oneToManyRelationCollection) {
          for (String relation : property.getRelationsFields()) {
            Field relationDeclaredField = ReflectionHelper.getFieldAccessible(
                oneToManyRelation.getClass(), relation);
            Object relationField = relationDeclaredField.get(oneToManyRelation);
            relationProperties.add(buildProperty(relation, relationField, property.isIgnoreDiff(), property.isDoNotOverride()));
          }
          entityRelations.add(entityRelationBuilder.properties(relationProperties).build());
        }
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      log.error("Error during parse field {}", e.getMessage());
      throw new VersioningException("Error during parse field " + e.getMessage(), e);
    }
    return propertyBuilder.oneToMany(entityRelations).build();
  }

  private static Entity buildEntity(Long actualVersionId, List<Property> properties) {
    return Entity.builder()
        .id(actualVersionId)
        .properties(properties)
        .build();
  }

  private static Property buildProperty(String fieldName, Object propertyValue,
      boolean ignoreDiff, boolean isDoNotOverride) {
    return Property.builder()
        .key(fieldName)
        .doNotOverride(isDoNotOverride)
        .ignoreDiff(ignoreDiff)
        .value(propertyValue)
        .build();
  }

}
