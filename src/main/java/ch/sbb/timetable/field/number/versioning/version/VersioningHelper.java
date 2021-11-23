package ch.sbb.timetable.field.number.versioning.version;

import static ch.sbb.timetable.field.number.versioning.date.DateHelper.areDatesSequential;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    return vd.isOnlyValidFromEdited() && vd.getEditedValidFrom().isBefore(toVersioning.getValidFrom());
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
        editedValidTo.isBefore(
            toVersioningList.get(toVersioningList.size() - 1).getValidTo());
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

  public static boolean isEditedValidToAfterTheRightBorderAndValidFromNotEdited(VersioningData vd,
      ToVersioning toVersioning) {
    return vd.getEditedValidTo().isAfter(toVersioning.getValidTo()) && vd.isOnlyValidToEdited();
  }

  public static boolean isOnlyValidToEditedAndPropertiesAreNotEdited(VersioningData vd) {
    return vd.isOnlyValidToEdited() && vd.getEditedEntity().getProperties().isEmpty();
  }

  public static boolean isOnlyValidToEditedAndPropertiesAreEdited(VersioningData vd) {
    return vd.isOnlyValidToEdited()
        && !vd.getEditedEntity().getProperties().isEmpty();
  }

  public static boolean isVersionOverTheRightBorder(ToVersioning rightBorderVersion,
      LocalDate editedValidFrom) {
    return editedValidFrom.isAfter(rightBorderVersion.getValidTo());
  }

  public static boolean isVersionOverTheLeftBorder(ToVersioning leftBorderVersion,
      LocalDate editedValidTo) {
    return editedValidTo.isBefore(leftBorderVersion.getValidFrom());
  }

  public static boolean areVersionsSequential(ToVersioning current, ToVersioning next) {
    return areDatesSequential(current.getValidTo(), next.getValidFrom());
  }

  public static boolean isThereGapBetweenVersions(List<ToVersioning> toVersioningList) {
    for (int i = 1; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i - 1);
      ToVersioning next = toVersioningList.get(i);
      if (!areDatesSequential(current.getValidTo(),
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
      if (!areDatesSequential(current.getValidTo(),
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
    return vd.isOnlyValidToEdited();
  }

  public static boolean areBothValidToAndValidFromChanged(VersioningData vd) {
    return !vd.isOnlyValidToEdited() && !vd.isOnlyValidFromEdited();
  }

  //  editedValidFrom is before current valid from
  //              editedValidFrom
  //  edited             |--------------|
  //  current |--------------|    |---------------|
  public static boolean isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(
      VersioningData vd, ToVersioning current) {
    return vd.getEditedValidFrom().isBefore(current.getValidTo())
        && !vd.getEditedValidFrom().equals(current.getValidFrom())
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
    return objectsToVersioning
        .stream()
        .filter(versioning -> versioning.getEntity().getId().equals(currentVersion.getId()))
        .findFirst()
        .orElse(null);
  }

  public static List<ToVersioning> findObjectToVersioningInValidFromValidToRange(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<ToVersioning> objectsToVersioning) {
    return objectsToVersioning.stream()
                              .filter(
                                  toVersioning -> !toVersioning.getValidFrom()
                                                               .isAfter(
                                                                   editedValidTo))
                              .filter(
                                  toVersioning -> !toVersioning.getValidTo()
                                                               .isBefore(
                                                                   editedValidFrom))
                              .collect(
                                  Collectors.toList());
  }

}
