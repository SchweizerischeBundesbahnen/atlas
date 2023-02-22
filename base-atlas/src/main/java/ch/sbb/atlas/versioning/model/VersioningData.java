package ch.sbb.atlas.versioning.model;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.versioning.exception.DateValidationException;
import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.version.VersioningHelper;
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

  private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
  private static final LocalDate MIN_DATE = LocalDate.of(1700, 1, 1);

  private final Versionable editedVersion;
  private final Versionable currentVersion;
  private final Entity editedEntity;
  private final List<ToVersioning> objectsToVersioning;
  private final List<ToVersioning> objectToVersioningFound;
  private final List<VersionedObject> versionedObjects;
  private LocalDate editedValidFrom;
  private LocalDate editedValidTo;
  private boolean onlyValidToEdited;
  private boolean onlyValidFromEdited;

  public VersioningData(Versionable editedVersion, Versionable currentVersion, Entity editedEntity,
      List<ToVersioning> objectsToVersioning) {
    this.editedVersion = editedVersion;
    this.currentVersion = currentVersion;
    this.editedEntity = editedEntity;
    this.objectsToVersioning = objectsToVersioning;
    this.populateValidFromAndValidTo(editedVersion);
    this.sortObjectsToVersioning(this.objectsToVersioning);
    this.objectToVersioningFound = VersioningHelper.findObjectToVersioningInValidFromValidToRange(
        this.editedValidFrom, this.editedValidTo, this.objectsToVersioning);
    this.versionedObjects = new ArrayList<>(
        VersionedObject.fillNotTouchedVersionedObject(this.objectsToVersioning,
            this.objectToVersioningFound));
  }

  public ToVersioning getSingleFoundObjectToVersioning() {
    if (VersioningHelper.isJustOneObjectToVersioningFound(this)) {
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

    validateValidFrom(this.editedValidFrom);
    validateValidTo(this.editedValidTo);
    validateDateRange(editedVersion);
  }

  private void validateDateRange(Versionable editedVersion) {
    if (editedVersion.getValidFrom() != null && editedVersion.getValidTo() != null
        && editedVersion.getValidFrom().isAfter(editedVersion.getValidTo())) {
      throw new DateValidationException(
          "Edited ValidFrom " + this.editedValidFrom + " is bigger than edited ValidTo "
              + this.editedValidTo);
    }
  }

  private void validateValidTo(LocalDate validTo) {
    if (validTo.isAfter(MAX_DATE)) {
      throw new DateValidationException("ValidTo cannot be after 31.12.9999.");
    }
  }

  private void validateValidFrom(LocalDate validFrom) {
    if (validFrom.isBefore(MIN_DATE)) {
      throw new DateValidationException("ValidFrom cannot be before 1.1.1700.");
    }
  }

  private void sortObjectsToVersioning(List<ToVersioning> objectsToVersioning) {
    objectsToVersioning.sort(comparing(ToVersioning::getValidFrom));
  }

}
