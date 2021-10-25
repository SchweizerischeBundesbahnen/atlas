package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
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
    List<AttributeObject> editedProperties = getEditedPropertyObjects(versionableProperties,current.getId(),
        edited);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = new ArrayList<>();
    for (Versionable version : currentVersions) {
      objectsToVersioning.add(
          new ToVersioning(version.getId(), version, getPropertyObjects(versionableProperties ,version)));
    }

    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(current, edited,
        editedProperties, objectsToVersioning);
    return versionedObjects;
  }


  <T extends Versionable> List<AttributeObject> getPropertyObjects(
      List<String> versionableProperties,T version) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        version);
    List<AttributeObject> attributeObjects = new ArrayList<>();

    for (String fieldName : versionableProperties) {
      attributeObjects.add(
          createPropertyObject(version.getId(), fieldName,
              String.valueOf(propertyAccessor.getPropertyValue(fieldName)))
      );
    }
    return attributeObjects;
  }

  <T extends Versionable> List<AttributeObject> getEditedPropertyObjects(
      List<String> versionableProperties,
      Long actualVersionId,
      T editedVersion) {
    ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(
        editedVersion);

    List<AttributeObject> editedAttributeObjects = new ArrayList<>();
    for (String fieldName : versionableProperties) {
      Object propertyValue = propertyAccessor.getPropertyValue(fieldName);
      if (propertyValue != null) {
        editedAttributeObjects.add(
            createPropertyObject(actualVersionId, fieldName,
                String.valueOf(propertyValue))
        );
      }
    }
    return editedAttributeObjects;
  }

  private AttributeObject createPropertyObject(Long objectId, String fieldName, String value) {
    AttributeObject changedAttributeName;
    changedAttributeName = new AttributeObject(objectId, fieldName, value);
    return changedAttributeName;
  }

}
