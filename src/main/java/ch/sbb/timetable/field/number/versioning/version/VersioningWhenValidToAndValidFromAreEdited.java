package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningWhenValidToAndValidFromAreEdited implements Versioning {

  @Override
  public List<VersionedObject> applyVersioning(VersioningData vd) {

    if (vd.isNoObjectToVersioningFound()) {
      VersioningWhenNoEntityFound versioningWhenNoEntityFound = new VersioningWhenNoEntityFound();
      List<VersionedObject> versionedObjectsOnNoObjectFound = versioningWhenNoEntityFound.applyVersioning(vd);
      vd.getVersionedObjects().addAll(versionedObjectsOnNoObjectFound);
    } else if (vd.isJustOneObjectToVersioningFound()) {
      VersioningOnSingleFoundEntity versioningOnSingleFoundEntity = new VersioningOnSingleFoundEntity();
      List<VersionedObject> versionedObjectsOnOnlyOneObjectFound =
          versioningOnSingleFoundEntity.applyVersioning(vd);
      vd.getVersionedObjects().addAll(versionedObjectsOnOnlyOneObjectFound);
    } else {
      VersioningOverMultipleFoundEntities versioningOverMultipleFoundEntities = new VersioningOverMultipleFoundEntities();
      List<VersionedObject> versionedObjectsOverMultipleEntity =
          versioningOverMultipleFoundEntities.applyVersioning(vd);
      vd.getVersionedObjects().addAll(versionedObjectsOverMultipleEntity);
    }

    return vd.getVersionedObjects();
  }

}
