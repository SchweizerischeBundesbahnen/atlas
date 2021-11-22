package ch.sbb.timetable.field.number.versioning.engine;

import static ch.sbb.timetable.field.number.versioning.convert.ConverterHelper.convertAllObjectsToVersioning;
import static ch.sbb.timetable.field.number.versioning.convert.ConverterHelper.convertToEditedEntity;
import static ch.sbb.timetable.field.number.versioning.merge.MergeHelper.mergeVersionedObject;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.areValidToAndValidFromNotEdited;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import ch.sbb.timetable.field.number.versioning.version.Versioning;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidFromAndValidToAreNotEdited;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidToAndValidFromAreEdited;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public <T extends Versionable> List<VersionedObject> applyVersioning(
      Versionable currentVersion, Versionable editedVersion,
      List<T> currentVersions, List<VersionableProperty> versionableProperties) {

    //2. get edited properties from editedVersion
    Entity editedEntity = convertToEditedEntity(currentVersion, editedVersion,
        versionableProperties);

    //3. collect all versions to versioning in ToVersioning object
    List<ToVersioning> objectsToVersioning = convertAllObjectsToVersioning(
        currentVersions, versionableProperties);

    VersioningData vd = new VersioningData(editedVersion, currentVersion, editedEntity,
        objectsToVersioning);

    Versioning versioning;
    if (areValidToAndValidFromNotEdited(editedVersion, currentVersion)) {
      //update actual version
      log.info("ValidFrom and ValidTo are not edited.");
      versioning = new VersioningWhenValidFromAndValidToAreNotEdited();
    } else {
      log.info("ValidFrom and/or ValidTo are edited.");
      versioning = new VersioningWhenValidToAndValidFromAreEdited();
    }
    List<VersionedObject> versionedObjects = new ArrayList<>(versioning.applyVersioning(vd));
    return mergeVersionedObject(versionedObjects);
  }


}
