package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.List;

public interface VersionableService {

  <T extends Versionable> List<VersionedObject> versioningObjects(Versionable current,
      Versionable edited,
      List<T> currentVersions);
}