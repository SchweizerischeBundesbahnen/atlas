package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.model.AttributeObject;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.List;

public interface VersionableService {

  List<VersionedObject> versioningObjects(Versionable actual, Versionable edited,
      List<AttributeObject> editedAttributeObjects,
      List<ToVersioning> objectsToVersioning);

  AttributeObject getAttributeObject(Long objectId, String fieldName, String value);

}