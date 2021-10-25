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
    Entity editedEntity = getEditedEntity(versionableProperties,
        current.getId(),
        edited);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = new ArrayList<>();
    for (Versionable currentVersion : currentVersions) {
      objectsToVersioning.add(
          new ToVersioning(currentVersion.getId(), currentVersion,
              buildEntity(versionableProperties, currentVersion)));
    }

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
      Property property = Property.builder()
                               .key(fieldName)
                               .value(String.valueOf(propertyAccessor.getPropertyValue(fieldName)))
                               .build();
      properties.add(property);
    }
    Entity entity = Entity.builder()
                          .id(version.getId())
                          .properties(properties)
                          .build();
    return entity;
  }

  <T extends Versionable> Entity getEditedEntity(
      List<String> versionableProperties,
      Long actualVersionId,
      T editedVersion) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        editedVersion);

    List<Property> properties = new ArrayList<>();
    for (String fieldName : versionableProperties) {
      Object propertyValue = propertyAccessor.getPropertyValue(fieldName);
      if (propertyValue != null) {
        Property property = Property.builder()
                                    .key(fieldName)
                                    .value(String.valueOf(propertyValue))
                                    .build();
        properties.add(property);
      }
    }
    Entity entity = Entity.builder()
                          .id(actualVersionId)
                          .properties(properties)
                          .build();
    return entity;
  }

}
