package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.date.DateHelper.areDatesSequential;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public final class VersioningHelper {

  private VersioningHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   *                  |___________|
   * |-----------|----------------------|--------------------|
   */
  public static boolean isEditedVersionInTheMiddleOfCurrentVersion(LocalDate editedValidFrom,
      LocalDate editedValidTo, ToVersioning toVersioning) {
    return editedValidFrom.isAfter(toVersioning.getVersionable().getValidFrom())
        && editedValidTo.isBefore(toVersioning.getVersionable().getValidTo());
  }

  /**
   *            |_____________________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isEditedVersionExactMatchingMultipleVersions(LocalDate editedValidFrom,
      LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.get(0).getVersionable().getValidFrom().equals(editedValidFrom) &&
        toVersioningList.get(toVersioningList.size() - 1)
                        .getVersionable()
                        .getValidTo()
                        .equals(editedValidTo);
  }

  /**
   * |_____________________|
   *            |----------|----------|----------|----------|----------|
   */
  public static boolean isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
      LocalDate editedValidFrom, LocalDate editedValidTo, ToVersioning toVersioning) {
    return editedValidTo.equals(toVersioning.getVersionable().getValidTo())
        && editedValidFrom != null
        && editedValidFrom.isBefore(toVersioning.getVersionable().getValidFrom());
  }

  /**
   *                             |______________________________|
   *            |----------|----------|----------|----------|----------|
   */
  public static boolean isBetweenMultipleVersionsAndOverTheBorders(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidFrom.isAfter(toVersioningList.get(0).getVersionable().getValidFrom())
        &&
        editedValidTo.isBefore(
            toVersioningList.get(toVersioningList.size() - 1).getVersionable().getValidTo());
  }

  /**
   *       |______________________________
   *            |----------|----------|----------|----------|----------|
   */
  public static boolean isOverTheLeftBorder(
      LocalDate editedValidFrom, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidFrom.isBefore(toVersioningList.get(0).getVersionable().getValidFrom());
  }

  /**
   *                                            ______________________________|
   *            |----------|----------|----------|----------|----------|
   */
  public static boolean isOverTheRightBorder(LocalDate editedValidTo, List<ToVersioning> toVersioningList) {
    return toVersioningList.size() > 1
        &&
        editedValidTo.isAfter(
            toVersioningList.get(toVersioningList.size() - 1).getVersionable().getValidTo());
  }

  /**
   *                                                    |______________|
   * |----------|----------|----------|----------|----------|
   */
  public static boolean isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder(
      LocalDate editedValidFrom, LocalDate editedValidTo, ToVersioning toVersioning) {
    return editedValidFrom != null &&
        (editedValidTo.equals(toVersioning.getVersionable().getValidTo()) || editedValidTo.isAfter(
            toVersioning.getVersionable()
                        .getValidTo()));
  }

  public static boolean isEditedValidToAfterTheRightBorder(LocalDate editedValidTo,
      ToVersioning toVersioning) {
    return editedValidTo.isAfter(toVersioning.getVersionable().getValidTo());
  }

  public static boolean isOnlyValidToEditedWithNoEditedProperties(Versionable editedVersion,
      Entity editedEntity) {
    return editedVersion.getValidTo() != null && editedVersion.getValidFrom() == null
        && editedEntity.getProperties().isEmpty();
  }

  public static boolean areValidToAndPropertiesEdited(Versionable editedVersion,
      Entity editedEntity) {
    return editedVersion.getValidTo() != null && editedVersion.getValidFrom() == null
        && !editedEntity.getProperties().isEmpty();
  }

  public static boolean isVersionOverTheRightBorder(ToVersioning rightBorderVersion,
      LocalDate editedValidFrom) {
    return editedValidFrom.isAfter(rightBorderVersion.getVersionable().getValidTo());
  }

  public static boolean isVersionOverTheLeftBorder(ToVersioning leftBorderVersion,
      LocalDate editedValidTo) {
    return editedValidTo.isBefore(leftBorderVersion.getVersionable().getValidFrom());
  }

  public static boolean isThereGapBetweenVersions(List<ToVersioning> toVersioningList) {
    for (int i = 1; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i - 1);
      ToVersioning next = toVersioningList.get(i);
      if (!areDatesSequential(current.getVersionable().getValidTo(),
          next.getVersionable().getValidFrom())) {
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
      if (!areDatesSequential(current.getVersionable().getValidTo(),
          next.getVersionable().getValidFrom())
          &&
          editedValidFrom.isAfter(current.getVersionable().getValidTo())
          && editedValidTo.isBefore(next.getVersionable().getValidFrom())
      ) {
        return current;
      }
    }
    return null;
  }

  public static boolean areValidToAndValidFromNotEdited(Versionable currentVersion,
      Versionable editedVersion) {
    return (editedVersion.getValidFrom() == null && editedVersion.getValidTo() == null) || (
        currentVersion.getValidFrom().equals(editedVersion.getValidFrom())
            && currentVersion.getValidTo().equals(editedVersion.getValidTo()));
  }

  public static boolean hasNextVersion(List<ToVersioning> toVersioningList, int index) {
    return (index + 1) < toVersioningList.size();
  }

  //TODO: check evan with properties?
  public static boolean isOnlyValidToChanged(Versionable editedVersion,
      Versionable currentVersion) {
    return (editedVersion.getValidFrom() == null ||
        currentVersion.getValidFrom().equals(editedVersion.getValidFrom()))
        && editedVersion.getValidTo() != null;
  }

  public static boolean areBothValidToAndValidFromChanged(Versionable editedVersion,
      Versionable currentVersion) {
    return (
        (editedVersion.getValidFrom() != null
            && !editedVersion.getValidFrom().equals(currentVersion.getValidFrom()))
            &&
            (editedVersion.getValidTo() != null
                && !editedVersion.getValidTo().equals(currentVersion.getValidTo()))
    );
  }

  //  editedValidFrom is before current valid from
  //              editedValidFrom
  //  edited             |--------------|
  //  current |--------------|    |---------------|
  public static boolean isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(
      LocalDate editedValidFrom, ToVersioning current) {
    return editedValidFrom.isBefore(current.getVersionable().getValidTo())
        && !editedValidFrom.equals(current.getVersionable().getValidFrom())
        && editedValidFrom.isAfter(current.getVersionable().getValidFrom());
  }

  public static boolean isEditedValidFromExactOnTheLeftBorder(LocalDate editedValidFrom,
      ToVersioning current) {
    return editedValidFrom.equals(current.getVersionable().getValidFrom());
  }

  public static boolean isEditedValidToExactOnTheRightBorder(LocalDate editedValidTo,
      ToVersioning toVersioning) {
    return editedValidTo.equals(toVersioning.getVersionable().getValidTo());
  }

  public static boolean isCurrentVersionBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, ToVersioning current) {
    return editedValidFrom.isBefore(current.getVersionable().getValidFrom())
        &&
        editedValidTo.isAfter(current.getVersionable().getValidTo());
  }

  public static List<ToVersioning> findObjectToVersioningInValidFromValidToRange(
      List<ToVersioning> objectsToVersioning,
      LocalDate editedValidFrom, LocalDate editedValidTo) {
    return objectsToVersioning.stream()
                              .filter(
                                  toVersioning -> !toVersioning.getVersionable()
                                                               .getValidFrom()
                                                               .isAfter(
                                                                   editedValidTo))
                              .filter(
                                  toVersioning -> !toVersioning.getVersionable()
                                                               .getValidTo()
                                                               .isBefore(
                                                                   editedValidFrom))
                              .collect(
                                  Collectors.toList());
  }

}
