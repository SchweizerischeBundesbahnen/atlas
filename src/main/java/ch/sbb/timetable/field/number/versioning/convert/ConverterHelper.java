package ch.sbb.timetable.field.number.versioning.convert;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Entity.EntityBuilder;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.Property.PropertyBuilder;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

public class ConverterHelper {

  public static <T extends Versionable> Entity convertToEditedEntity(
      List<VersionableProperty> versionableProperties,
      Long actualVersionId,
      T editedVersion) {

    List<Property> properties = extractProperties(versionableProperties, editedVersion);
    List<Property> propertiesNotEmpty = properties.stream()
                                                  .filter(Property::isNotEmpty)
                                                  .collect(Collectors.toList());
    return buildEntity(actualVersionId, propertiesNotEmpty);
  }

  public static <T extends Versionable> List<ToVersioning> convertAllObjectsToVersioning(
      List<VersionableProperty> versionableProperties, List<T> currentVersions) {
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

    List<Property> properties = extractProperties(
        versionableProperties, version);
    return buildEntity(version.getId(), properties);
  }

  private static  <T extends Versionable> List<Property> extractProperties(List<VersionableProperty> versionableProperties,
      T version) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);
    List<Property> properties = new ArrayList<>();
    for (VersionableProperty property : versionableProperties) {
      if (RelationType.NONE == property.getRelationType()) {
        Object propertyValue = propertyAccessor.getPropertyValue(property.getFieldName());
        properties.add(buildProperty(property.getFieldName(), propertyValue));
      }
      if (RelationType.ONE_TO_MANY == property.getRelationType()) {
        Property extractOneToManyRelationProperty = extractOneToManyRelationProperty(
            propertyAccessor,
            property);
        properties.add(extractOneToManyRelationProperty);
      }
    }
    return properties;
  }

  private static Property extractOneToManyRelationProperty(
      ConfigurablePropertyAccessor propertyAccessor,
      VersionableProperty property) {
    PropertyBuilder propertyBuilder = Property.builder().key(property.getFieldName());
    List<Entity> entityRelations = new ArrayList<>();

    Object relationFields = propertyAccessor.getPropertyValue(property.getFieldName());
    //OneToMany relation
    if (relationFields instanceof Collection) {
      for (Object relationField : ((Collection<Object>) relationFields)) {
        ConfigurablePropertyAccessor relationFieldAccess = PropertyAccessorFactory.forDirectFieldAccess(
            relationField);
        List<Property> relationProperties = new ArrayList<>();
        EntityBuilder entityRelationBuilder = Entity.builder();
        for (String relation : property.getRelationsFields()) {
          //TODO: try to find a better solution. Maybe remove it
          if ("id".equals(relation)) {
            entityRelationBuilder.id((Long) relationFieldAccess.getPropertyValue(relation));
          } else {
            relationProperties.add(
                buildProperty(relation, relationFieldAccess.getPropertyValue(relation)));
          }
        }
        entityRelations.add(entityRelationBuilder.properties(relationProperties).build());
      }
    }
    return propertyBuilder.oneToMany(entityRelations).build();
  }

  private static Entity buildEntity(Long actualVersionId, List<Property> properties) {
    return Entity.builder()
                 .id(actualVersionId)
                 .properties(properties)
                 .build();
  }

  private static Property buildProperty(String fieldName, Object propertyValue) {
    return Property.builder()
                   .key(fieldName)
                   .value(propertyValue != null ? String.valueOf(propertyValue) : null)
                   .build();
  }

}
