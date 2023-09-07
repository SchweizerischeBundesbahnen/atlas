package ch.sbb.atlas.versioning.version;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.List;

public final class VersioningHelper {

  private VersioningHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * |___________|
   * |-----------|----------------------|--------------------|
   */
  public static boolean isEditedVersionInTheMiddleOfCurrentEntity(LocalDate editedValidFrom,
      LocalDate editedValidTo, ToVersioning toVersioning) {
    return editedValidFrom.isAfter(toVersioning.getValidFrom())
        && editedValidTo.isBefore(toVersioning.getValidTo());
  }

  /**
   * |_____________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isEditedVersionExactMatchingMultipleEntities(LocalDate editedValidFrom,
      LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.get(0).getValidFrom().equals(editedValidFrom) &&
        toVersioningList.get(toVersioningList.size() - 1)
                        .getValidTo()
                        .equals(editedValidTo);
  }

  /**
   * |_____________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
      VersioningData vd, ToVersioning toVersioning) {
    return isOnlyValidFromEdited(vd) && vd.getEditedValidFrom()
                                          .isBefore(toVersioning.getValidFrom());
  }

  /**
   * |______________________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isBetweenMultipleVersionsAndOverTheBorders(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidFrom.isAfter(toVersioningList.get(0).getValidFrom())
        &&
        editedValidTo.isBefore(toVersioningList.get(toVersioningList.size() - 1).getValidTo());
  }

  /**
   * |___________________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isBetweenMultipleVersionsAndStartsOnABorder(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidFrom.equals(toVersioningList.get(0).getValidFrom())
        &&
        editedValidTo.isBefore(toVersioningList.get(toVersioningList.size() - 1).getValidTo());
  }

  /**
   * |___________________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isBetweenMultipleVersionsAndEndsOnABorder(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidFrom.isAfter(toVersioningList.get(0).getValidFrom())
        &&
        editedValidTo.equals(toVersioningList.get(toVersioningList.size() - 1).getValidTo());
  }

  /**
   * |______________________________
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isEditedValidFromOverTheLeftBorder(
      LocalDate editedValidFrom, List<ToVersioning> toVersioningList) {
    if (toVersioningList.size() <= 1) {
      throw new VersioningException("toVersioningList size must be bigger than 1.");
    }
    return editedValidFrom.isBefore(toVersioningList.get(0).getValidFrom());
  }

  /**
   * |______________|
   * |----------|
   */
  public static boolean isEditedValidFromOverTheLeftBorderAndEndsWithin(
      VersioningData versioningData) {
    if (versioningData.getObjectsToVersioning().size() != 1) {
      throw new VersioningException("toVersioningList size must be 1.");
    }
    return versioningData.getEditedValidFrom()
                         .isBefore(versioningData.getObjectsToVersioning().get(0).getValidFrom())
        && versioningData.getEditedValidTo()
                         .isBefore(versioningData.getObjectsToVersioning().get(0).getValidTo());
  }

  /**
   * ______________________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isEditedValidToOverTheRightBorder(LocalDate editedValidTo,
      List<ToVersioning> toVersioningList) {
    if (toVersioningList.size() <= 1) {
      throw new VersioningException("toVersioningList size must be bigger than 1.");
    }
    return editedValidTo.isAfter(
        toVersioningList.get(toVersioningList.size() - 1).getValidTo());
  }

  /**
   * |______________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isOnTheRightBorderAndValidToIsOnOrOverTheBorder(
      VersioningData vd, ToVersioning toVersioning) {
    return vd.getEditedValidFrom() != null &&
        (vd.getEditedValidTo().equals(toVersioning.getValidTo()) || vd.getEditedValidTo().isAfter(
            toVersioning.getValidTo()));
  }

  /**
   * |_____|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isOnBeginningOfVersionAndEndingWithin(
      VersioningData vd, ToVersioning toVersioning) {
    return vd.getEditedValidFrom() != null
        && vd.getEditedValidFrom().equals(toVersioning.getValidFrom())
        && vd.getEditedValidTo().isBefore(toVersioning.getValidTo());
  }

  public static boolean isEditedValidToAfterTheRightBorderAndValidFromNotEdited(VersioningData vd,
      ToVersioning toVersioning) {
    return vd.getEditedValidTo().isAfter(toVersioning.getValidTo()) && isOnlyValidToEdited(
        vd);
  }

  public static boolean isOnlyValidToEditedAndPropertiesAreNotEdited(VersioningData vd) {
    return isOnlyValidToEdited(vd) && !arePropertiesEdited(vd);
  }

  public static boolean isOnlyValidFromEditedAndPropertiesAreNotEdited(VersioningData vd) {
    return isOnlyValidFromEdited(vd) && !arePropertiesEdited(vd);
  }

  public static boolean isOnlyValidToEditedAndPropertiesAreEdited(VersioningData vd) {
    return isOnlyValidToEdited(vd) && arePropertiesEdited(vd);
  }

  public static boolean arePropertiesEdited(VersioningData vd) {
    return !vd.getEditedEntity().getProperties().isEmpty();
  }

  public static boolean isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
      ToVersioning toVersioning, VersioningData vd) {
    return vd.getEditedValidFrom().isAfter(toVersioning.getValidFrom()) && vd.getEditedValidTo()
                                                                             .isBefore(
                                                                                 toVersioning.getValidTo())
        && !arePropertiesEdited(vd);
  }

  public static boolean isSingularVersionAndPropertiesAreNotEdited(VersioningData vd) {
    return vd.getObjectsToVersioning().size() == 1 && !arePropertiesEdited(vd);
  }

  public static boolean isVersionOverTheRightBorder(ToVersioning rightBorderVersion,
      LocalDate editedValidFrom) {
    return editedValidFrom.isAfter(rightBorderVersion.getValidTo());
  }

  public static boolean isVersionOverTheLeftBorder(ToVersioning leftBorderVersion,
      LocalDate editedValidTo) {
    return editedValidTo.isBefore(leftBorderVersion.getValidFrom());
  }

  public static boolean isVersionOverTheLeftAndTheRightBorder(VersioningData vd) {
    return vd.getEditedValidFrom().isBefore(vd.getCurrentVersion().getValidFrom())
        && vd.getEditedValidTo().isAfter(vd.getCurrentVersion().getValidTo());
  }

  public static boolean areVersionsSequential(ToVersioning current, ToVersioning next) {
    return DateHelper.areDatesSequential(current.getValidTo(), next.getValidFrom());
  }

  public static boolean isThereGapBetweenVersions(List<ToVersioning> toVersioningList) {
    for (int i = 1; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i - 1);
      ToVersioning next = toVersioningList.get(i);
      if (!DateHelper.areDatesSequential(current.getValidTo(),
          next.getValidFrom())) {
        return true;
      }
    }
    return false;
  }

  public static ToVersioning getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    for (int i = 1; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i - 1);
      ToVersioning next = toVersioningList.get(i);
      if (!DateHelper.areDatesSequential(current.getValidTo(),
          next.getValidFrom())
          &&
          editedValidFrom.isAfter(current.getValidTo())
          && editedValidTo.isBefore(next.getValidFrom())
      ) {
        return current;
      }
    }
    return null;
  }

  public static boolean areValidToAndValidFromNotEdited(Versionable editedVersion,
      Versionable currentVersion) {
    return (editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) || (
        currentVersion.getValidFrom().equals(editedVersion.getValidFrom())
            && currentVersion.getValidTo().equals(editedVersion.getValidTo()));
  }

  public static boolean hasNextVersion(List<ToVersioning> toVersioningList, int index) {
    return (index + 1) < toVersioningList.size();
  }

  public static boolean isOnlyValidToChanged(VersioningData vd) {
    return isOnlyValidToEdited(vd);
  }

  public static boolean areBothValidToAndValidFromChanged(VersioningData vd) {
    return !isOnlyValidToEdited(vd) && !isOnlyValidFromEdited(vd);
  }

  //  editedValidFrom is before current valid from
  //              editedValidFrom
  //  edited             |--------------|
  //  current |--------------|    |---------------|
  public static boolean isEditedValidFromAfterCurrentValidFromAndBeforeOrEqualCurrentValidTo(
      VersioningData vd, ToVersioning current) {
    return !vd.getEditedValidFrom().isAfter(current.getValidTo())
        && vd.getEditedValidFrom().isAfter(current.getValidFrom());
  }

  public static boolean isEditedValidFromExactOnTheLeftBorder(LocalDate editedValidFrom,
      ToVersioning current) {
    return editedValidFrom.equals(current.getValidFrom());
  }

  public static boolean isEditedValidToExactOnTheRightBorder(LocalDate editedValidTo,
      ToVersioning toVersioning) {
    return editedValidTo.equals(toVersioning.getValidTo());
  }

  public static boolean isCurrentVersionBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, ToVersioning current) {
    return editedValidFrom.isBefore(current.getValidFrom())
        &&
        editedValidTo.isAfter(current.getValidTo());
  }

  public static ToVersioning findObjectToVersioning(Versionable currentVersion,
      List<ToVersioning> objectsToVersioning) {
    return getToVersioningToCompare(objectsToVersioning, currentVersion.getId());
  }

  public static List<ToVersioning> findObjectToVersioningInValidFromValidToRange(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> objectsToVersioning) {
    return objectsToVersioning.stream()
                              .filter(toVersioning ->
                                  !toVersioning.getValidFrom().isAfter(editedValidTo))
                              .filter(toVersioning ->
                                  !toVersioning.getValidTo().isBefore(editedValidFrom))
                              .toList();
  }

  public static boolean isNoObjectToVersioningFound(VersioningData versioningData) {
    return versioningData.getObjectToVersioningFound().isEmpty();
  }

  public static boolean isJustOneObjectToVersioningFound(VersioningData versioningData) {
    return versioningData.getObjectToVersioningFound().size() == 1;
  }

  public static boolean isOnlyValidFromEdited(VersioningData versioningData) {
    return versioningData.getEditedVersion().getValidFrom() != null
        && (!versioningData.getEditedVersion()
                           .getValidFrom()
                           .equals(versioningData.getCurrentVersion().getValidFrom()))
        && (versioningData.getEditedVersion().getValidTo() == null
        || versioningData.getEditedVersion().getValidTo()
                         .equals(
                             versioningData.getCurrentVersion().getValidTo()));
  }

  public static boolean isOnlyValidToEdited(VersioningData versioningData) {
    return versioningData.getEditedVersion().getValidTo() != null
        && (!versioningData.getEditedVersion()
                           .getValidTo()
                           .equals(versioningData.getCurrentVersion().getValidTo()))
        && (versioningData.getEditedVersion().getValidFrom() == null
        || versioningData.getEditedVersion().getValidFrom()
                         .equals(
                             versioningData.getCurrentVersion().getValidFrom()));
  }

  public static boolean isCurrentVersionFirstVersion(VersioningData versioningData) {
    return versioningData.getCurrentVersion()
                         .getValidFrom()
                         .equals(versioningData.getObjectsToVersioning().get(0).getValidFrom());
  }

  public static boolean checkChangesAfterVersioning(VersioningData vd,
      List<VersionedObject> mergedVersionedObjects) {
    if (getNewVersionsCount(mergedVersionedObjects) > 0) {
      return true;
    }
    for (VersionedObject versionedObject : mergedVersionedObjects) {
      ToVersioning toVersioningToCompare = getToVersioningToCompare(vd.getObjectsToVersioning(),
          versionedObject.getEntity().getId());
      if (toVersioningToCompare == null) {
        throw new IllegalStateException("At this point toVersioning cannot be null!");
      }
      if (!areEquals(versionedObject, toVersioningToCompare)) {
        return true;
      }
    }
    return false;
  }

  private static ToVersioning getToVersioningToCompare(List<ToVersioning> objectsToVersioning,
      Long id) {
    return objectsToVersioning
        .stream()
        .filter(toVersioning -> toVersioning.getEntity().getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  private static long getNewVersionsCount(List<VersionedObject> mergedVersionedObjects) {
    return mergedVersionedObjects.stream()
                                 .filter(
                                     versionedObject -> versionedObject.getEntity().getId() == null)
                                 .count();
  }

  private static boolean areEquals(VersionedObject versionedObject,
      ToVersioning toVersioningToCompare) {
    return toVersioningToCompare.getEntity().equals(versionedObject.getEntity())
        && toVersioningToCompare.getValidFrom().equals(versionedObject.getValidFrom())
        && toVersioningToCompare.getValidTo().equals(versionedObject.getValidTo());
  }
}
