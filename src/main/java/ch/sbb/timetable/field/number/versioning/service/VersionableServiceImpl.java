package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.ArrayList;
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
      List<String> versionableProperties, Versionable current,
      Versionable edited,
      List<T> currentVersions) {

    //2. get edited properties from editedVersion
    Entity editedEntity = convertToEditedEntity(versionableProperties,
        current.getId(),
        edited);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = getAllObjectsToVersioning(
        versionableProperties, currentVersions);

    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(current, edited,
        editedEntity, objectsToVersioning);
    return versionedObjects;
  }

  <T extends Versionable> Entity buildEntity(
      List<String> versionableProperties, T version) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);

    List<Property> properties = new ArrayList<>();
    for (String fieldName : versionableProperties) {
      properties.add(buildProperty(fieldName, propertyAccessor.getPropertyValue(fieldName)));
    }
    return buildEntity(version.getId(), properties);
  }

  <T extends Versionable> Entity convertToEditedEntity(
      List<String> versionableProperties,
      Long actualVersionId,
      T editedVersion) {

    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        editedVersion);

    List<Property> properties = new ArrayList<>();
    for (String fieldName : versionableProperties) {
      Object propertyValue = propertyAccessor.getPropertyValue(fieldName);
      if (propertyValue != null) {
        properties.add(buildProperty(fieldName, propertyValue));
      }
    }
    return buildEntity(actualVersionId, properties);
  }

  private <T extends Versionable> List<ToVersioning> getAllObjectsToVersioning(
      List<String> versionableProperties, List<T> currentVersions) {
    List<ToVersioning> objectsToVersioning = new ArrayList<>();
    for (Versionable currentVersion : currentVersions) {
      objectsToVersioning.add(
          ToVersioning.builder()
                      .versionable(currentVersion)
                      .entity(buildEntity(versionableProperties, currentVersion))
                      .build()
      );
    }
    return objectsToVersioning;
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
