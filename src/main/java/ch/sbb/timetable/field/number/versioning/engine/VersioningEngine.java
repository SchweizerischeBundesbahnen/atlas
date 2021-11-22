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
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidToAndOrValidFromAreEdited;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public <T extends Versionable> List<VersionedObject> applyVersioning(
      Versionable currentVersion, Versionable editedVersion,
      List<T> currentVersions, List<VersionableProperty> versionableProperties) {

    Entity editedEntity = convertToEditedEntity(currentVersion, editedVersion,
        versionableProperties);

    List<ToVersioning> objectsToVersioning = convertAllObjectsToVersioning(
        currentVersions, versionableProperties);

    VersioningData vd = new VersioningData(editedVersion, currentVersion, editedEntity,
        objectsToVersioning);

    Versioning versioning;
    if (areValidToAndValidFromNotEdited(editedVersion, currentVersion)) {
      log.info("ValidFrom and ValidTo are not edited.");
      versioning = new VersioningWhenValidFromAndValidToAreNotEdited();
    } else {
      log.info("ValidFrom and/or ValidTo are edited.");
      versioning = new VersioningWhenValidToAndOrValidFromAreEdited();
    }
    List<VersionedObject> versionedObjects = new ArrayList<>(versioning.applyVersioning(vd));
    return mergeVersionedObject(versionedObjects);
  }

}
