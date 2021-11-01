package ch.sbb.timetable.field.number.versioning;

import ch.sbb.timetable.field.number.versioning.convert.ConverterHelper;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.version.Versioning;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenOnlyValidFromIsEdited;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenOnlyValidToIsEdited;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidFromAndValidToAreNotEdited;
import ch.sbb.timetable.field.number.versioning.version.VersioningWhenValidToAndValidFromAreEdited;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningEngine {

  public <T extends Versionable> List<VersionedObject> applyVersioning(List<VersionableProperty> versionableProperties,
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

    List<VersionedObject> versionedObjects = new ArrayList<>();

    Versioning versioning;
    //validFrom and validTo are not modified
    if ((editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) || (
        currentVersion.getValidFrom().equals(editedVersion.getValidFrom())
            || currentVersion.getValidTo().equals(editedVersion.getValidTo()))) {
      //update actual version
      versioning = new VersioningWhenValidFromAndValidToAreNotEdited();
      return versioning.applyVersioning(currentVersion, editedEntity, objectsToVersioning);
    }

    //only validFrom is modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() == null) {
      //Only validFrom is edited
      versioning = new VersioningWhenOnlyValidFromIsEdited();
      return versioning.applyVersioning(editedVersion, currentVersion, objectsToVersioning,
          editedEntity);
    }

    //only validTo is modified
    if (editedVersion.getValidFrom() == null && editedVersion.getValidTo() != null) {
      //get all versions between actual.getValidFrom() and edited.getValidTo()
      versioning = new VersioningWhenOnlyValidToIsEdited();
      return versioning.applyVersioning(editedVersion, currentVersion, objectsToVersioning,
          editedEntity);
    }

    //validFrom and validTo are modified
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null) {
      //get all versions between editedVersion.getValidFrom() and editedVersion.getValidTo()
      versioning = new VersioningWhenValidToAndValidFromAreEdited();
      return versioning.applyVersioning(editedVersion, currentVersion, objectsToVersioning,
          editedEntity);
    }

    return versionedObjects;
  }

}
