package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.ObjectProperty;
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
  public <T extends Versionable> List<VersionedObject> versioningObjects(List<String> versionableProperties,Versionable current,
      Versionable edited,
      List<T> currentVersions) {
    //2. get edited properties from editedVersion
    List<ObjectProperty> editedProperties = getEditedObjectProperties(versionableProperties,current.getId(),
        edited);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = new ArrayList<>();
    for (Versionable version : currentVersions) {
      objectsToVersioning.add(
          new ToVersioning(version.getId(), version, getObjectProperties(versionableProperties ,version)));
    }

    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(current, edited,
        editedProperties, objectsToVersioning);
    return versionedObjects;
  }


  <T extends Versionable> List<ObjectProperty> getObjectProperties(
      List<String> versionableProperties,T version) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);
    List<ObjectProperty> objectProperties = new ArrayList<>();

    for (String fieldName : versionableProperties) {
      objectProperties.add(
          createObjectProperty(version.getId(), fieldName,
              String.valueOf(propertyAccessor.getPropertyValue(fieldName)))
      );
    }
    return objectProperties;
  }

  <T extends Versionable> List<ObjectProperty> getEditedObjectProperties(
      List<String> versionableProperties,
      Long actualVersionId,
      T editedVersion) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        editedVersion);

    List<ObjectProperty> editedObjectProperties = new ArrayList<>();
    for (String fieldName : versionableProperties) {
      Object propertyValue = propertyAccessor.getPropertyValue(fieldName);
      if (propertyValue != null) {
        editedObjectProperties.add(
            createObjectProperty(actualVersionId, fieldName,
                String.valueOf(propertyValue))
        );
      }
    }
    return editedObjectProperties;
  }

  private ObjectProperty createObjectProperty(Long objectId, String fieldName, String value) {
    ObjectProperty changedAttributeName;
    changedAttributeName = new ObjectProperty(objectId, fieldName, value);
    return changedAttributeName;
  }

}
