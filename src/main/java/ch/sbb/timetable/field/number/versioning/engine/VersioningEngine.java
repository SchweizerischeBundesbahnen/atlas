package ch.sbb.timetable.field.number.versioning.engine;

import ch.sbb.timetable.field.number.versioning.convert.ConverterHelper;
import ch.sbb.timetable.field.number.versioning.merge.MergeHelper;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.version.Versioning;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidFromAndValidToAreNotEdited;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidToAndValidFromAreEdited;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public <T extends Versionable> List<VersionedObject> applyVersioning(
      List<VersionableProperty> versionableProperties,
      Versionable currentVersion,
      Versionable editedVersion,
      List<T> currentVersions) {

    //2. get edited properties from editedVersion
    Entity editedEntity = ConverterHelper.convertToEditedEntity(versionableProperties,
        currentVersion.getId(), editedVersion);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = ConverterHelper.convertAllObjectsToVersioning(
        versionableProperties, currentVersions);
    //Temporal sort objects versioning
    objectsToVersioning.sort(Comparator.comparing(o -> o.getVersionable().getValidFrom()));

    Versioning versioning;
    List<VersionedObject> versionedObjects = new ArrayList<>();
    if (areValidToAndValidFromNotEdited(currentVersion, editedVersion)) {
      //update actual version
      log.info("ValidFrom and ValidTo are not edited.");
      versioning = new VersioningWhenValidFromAndValidToAreNotEdited();
       versionedObjects.addAll( versioning.applyVersioning(currentVersion,
          editedEntity, objectsToVersioning));
    }
    else {
      log.info("ValidFrom and ValidTo are edited.");
      versioning = new VersioningWhenValidToAndValidFromAreEdited();
      versionedObjects.addAll( versioning.applyVersioning(editedVersion,
          currentVersion, objectsToVersioning,
          editedEntity));
    }
    List<VersionedObject> versionedObjectsMerged = MergeHelper.mergeVersionedObject(versionedObjects);
    return versionedObjectsMerged;
//    return versionedObjects;
  }



  public boolean areValidToAndValidFromNotEdited(Versionable currentVersion,
      Versionable editedVersion) {
    return (editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) || (
        currentVersion.getValidFrom().equals(editedVersion.getValidFrom())
            && currentVersion.getValidTo().equals(editedVersion.getValidTo()));
  }

}
