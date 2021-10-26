package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Entity.EntityBuilder;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.Property.PropertyBuilder;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

public class VersionableServiceImpl implements VersionableService {

  private final VersioningEngine versioningEngine;

  public VersionableServiceImpl() {
    this.versioningEngine = new VersioningEngine();
  }

  @Override
  public <T extends Versionable> List<VersionedObject> versioningObjects(
      List<VersionableProperty> versionableProperties, Versionable current,
      Versionable edited,
      List<T> currentVersions) {

    //2. get edited properties from editedVersion
    Entity editedEntity = convertToEditedEntity(versionableProperties, current.getId(), edited);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = getAllObjectsToVersioning(
        versionableProperties, currentVersions);

    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(current, edited,
        editedEntity, objectsToVersioning);
    return versionedObjects;
  }

  private <T extends Versionable> Entity convertToEditedEntity(
      List<VersionableProperty> versionableProperties,
      Long actualVersionId,
      T editedVersion) {

    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        editedVersion);

    List<Property> properties = new ArrayList<>();
    for (VersionableProperty property : versionableProperties) {
      if (RelationType.NONE == property.getRelationType()) {
        Object propertyValue = propertyAccessor.getPropertyValue(property.getFieldName());
        if (propertyValue != null) {
          properties.add(buildProperty(property.getFieldName(), propertyValue));
        }
      }
      if (RelationType.ONE_TO_MANY == property.getRelationType()) {
        Property extractOneToManyRelationProperty = extractOneToManyRelationProperty(
            propertyAccessor,
            property);
        properties.add(extractOneToManyRelationProperty);
      }
    }
    return buildEntity(actualVersionId, properties);
  }

  private Property extractOneToManyRelationProperty(ConfigurablePropertyAccessor propertyAccessor,
      VersionableProperty property) {
    PropertyBuilder propertyBuilder = Property.builder().key(property.getFieldName());
    List<Entity> entityRelations = new ArrayList<>();
    Object relationFields = propertyAccessor.getPropertyValue(property.getFieldName());
    if (relationFields instanceof Collection) {
      for (Object relationField : ((Collection<Object>) relationFields)) {
        ConfigurablePropertyAccessor relationFieldAccess = PropertyAccessorFactory.forDirectFieldAccess(
            relationField);
        List<Property> relationProperties = new ArrayList<>();
        EntityBuilder entityRelationBuilder = Entity.builder();
        for (String relation : property.getRelationsFields()) {
          //TODO: try to find a better solution
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

  private <T extends Versionable> List<ToVersioning> getAllObjectsToVersioning(
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

  private <T extends Versionable> Entity convertToEntity(
      List<VersionableProperty> versionableProperties, T version) {
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
    return buildEntity(version.getId(), properties);
  }


  private Entity buildEntity(Long actualVersionId, List<Property> properties) {
    return Entity.builder()
                 .id(actualVersionId)
                 .properties(properties)
                 .build();
  }

  private Property buildProperty(String fieldName, Object propertyValue) {
    return Property.builder()
                   .key(fieldName)
                   .value(String.valueOf(propertyValue))
                   .build();
  }

}
