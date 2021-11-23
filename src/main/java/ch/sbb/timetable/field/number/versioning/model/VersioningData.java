package ch.sbb.timetable.field.number.versioning.model;

import static ch.sbb.timetable.field.number.versioning.model.VersionedObject.fillNotTouchedVersionedObject;
import static ch.sbb.timetable.field.number.versioning.version.VersioningHelper.findObjectToVersioningInValidFromValidToRange;
import static java.util.Comparator.comparing;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
public class VersioningData {

  private final Versionable editedVersion;
  private final Versionable currentVersion;
  private LocalDate editedValidFrom;
  private LocalDate editedValidTo;
  private final Entity editedEntity;
  private final List<ToVersioning> objectsToVersioning;
  private final List<ToVersioning> objectToVersioningFound;
  private final List<VersionedObject> versionedObjects;

  public VersioningData(Versionable editedVersion, Versionable currentVersion, Entity editedEntity,
      List<ToVersioning> objectsToVersioning) {
    this.editedVersion = editedVersion;
    this.currentVersion = currentVersion;
    this.editedEntity = editedEntity;
    this.objectsToVersioning = objectsToVersioning;
    this.populateValidFromAndValidTo(editedVersion);
    this.sortObjectsToVersioning(this.objectsToVersioning);
    this.objectToVersioningFound = findObjectToVersioningInValidFromValidToRange(
        this.editedValidFrom, this.editedValidTo, this.objectsToVersioning);
    this.versionedObjects = new ArrayList<>(
        fillNotTouchedVersionedObject(this.objectsToVersioning, this.objectToVersioningFound));
  }

  public boolean isNoObjectToVersioningFound() {
    return this.objectToVersioningFound.isEmpty();
  }

  public boolean isJustOneObjectToVersioningFound() {
    return this.objectToVersioningFound.size() == 1;
  }

  public ToVersioning getSingleFoundObjectToVersioning() {
    if (isJustOneObjectToVersioningFound()) {
      return this.objectToVersioningFound.get(0);
    }
    throw new VersioningException("Found more or less than one object to versioning.");
  }

  private void populateValidFromAndValidTo(Versionable editedVersion) {
    this.editedValidFrom = editedVersion.getValidFrom();
    if (this.editedValidFrom == null) {
      log.info("ValidFrom not edited.");
      this.editedValidFrom = currentVersion.getValidFrom();
    }

    this.editedValidTo = editedVersion.getValidTo();
    if (this.editedValidTo == null) {
      log.info("ValidTo not edited.");
      this.editedValidTo = currentVersion.getValidTo();
    }

    if (this.editedValidFrom.isAfter(this.editedValidTo)) {
      throw new VersioningException(
          "Edited ValidFrom " + this.editedValidFrom + " is bigger than edited ValidTo "
              + this.editedValidTo);
    }
  }

  private void sortObjectsToVersioning(List<ToVersioning> objectsToVersioning) {
    objectsToVersioning.sort(comparing(ToVersioning::getValidFrom));
  }

}
