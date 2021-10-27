package ch.sbb.timetable.field.number.versioning.service;

import ch.sbb.timetable.field.number.versioning.VersioningEngine;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import java.util.List;

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

    List<VersionedObject> versionedObjects = versioningEngine.applyVersioning(versionableProperties,
        current, edited, currentVersions);
    return versionedObjects;
  }

}
