package ch.sbb.timetable.field.number.versioning.version;

import ch.sbb.timetable.field.number.versioning.date.DateHelper;
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
   * |___________|
   * |-----------|----------------------|--------------------|
   */
  public static boolean isEditedVersionInTheMiddleOfCurrentVersion(LocalDate editedValidFrom,
      LocalDate editedValidTo, ToVersioning toVersioning) {
    return editedValidFrom.isAfter(toVersioning.getVersionable().getValidFrom())
        && editedValidTo.isBefore(toVersioning.getVersionable().getValidTo());
  }

  /**
   * |_____________________|
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

  public static boolean isEditedValidToAfterTheRightBorder(LocalDate editedValidTo,
      ToVersioning toVersioning) {
    return editedValidTo.isAfter(toVersioning.getVersionable().getValidTo());
  }

  public static boolean isOnlyValidToEditedWithNoEditedProperties(Versionable editedVersion,
      Entity editedEntity) {
    return editedVersion.getValidTo() != null && editedVersion.getValidFrom() == null
        && editedEntity.getProperties().isEmpty();
  }

  public static boolean isValidToEditedAndPropertiesAreEdited(Versionable editedVersion,
      Entity editedEntity) {
    return editedVersion.getValidTo() != null && editedVersion.getValidFrom() == null
        && !editedEntity.getProperties().isEmpty();
  }

  public static boolean isVersionOnTheRightBorder(ToVersioning rightBorderVersion,
      LocalDate editedValidFrom) {
    return editedValidFrom.isAfter(rightBorderVersion.getVersionable().getValidTo());
  }

  public static boolean isVersionOnTheLeftBorder(ToVersioning leftBorderVersion,
      LocalDate editedValidTo) {
    return editedValidTo.isBefore(leftBorderVersion.getVersionable().getValidFrom());
  }

  public static boolean isThereGapBetweenVersions(List<ToVersioning> toVersioningList) {
    for (int i = 1; i < toVersioningList.size(); i++) {
      ToVersioning current = toVersioningList.get(i - 1);
      ToVersioning next = toVersioningList.get(i);
      if (!DateHelper.areDatesSequential(current.getVersionable().getValidTo(),
          next.getVersionable().getValidFrom())) {
        return true;
      }
    }
    return false;
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
