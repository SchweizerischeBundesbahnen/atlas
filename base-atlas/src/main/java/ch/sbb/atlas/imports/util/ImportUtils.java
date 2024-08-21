package ch.sbb.atlas.imports.util;

import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImportUtils {

  public <T extends Versionable> T getCurrentVersion(List<T> dbVersions, LocalDate editedValidFrom,
      LocalDate editedValidTo) {
    dbVersions.sort(Comparator.comparing(Versionable::getValidFrom));

    Optional<T> currentVersionMatch = dbVersions.stream()
        .filter(dbVersion -> {
              // match validFrom
              if (editedValidFrom.isEqual(dbVersion.getValidFrom())) {
                return true;
              }
              // match validTo
              if (editedValidTo.isEqual(dbVersion.getValidTo())) {
                return true;
              }
              // match edited version between dbVersion
              if (editedValidFrom.isAfter(dbVersion.getValidFrom()) && editedValidTo.isBefore(dbVersion.getValidTo())) {
                return true;
              }
              if (editedValidFrom.isAfter(dbVersion.getValidFrom()) && editedValidFrom.isBefore(dbVersion.getValidTo())) {
                return true;
              }
              if (editedValidTo.isAfter(dbVersion.getValidFrom()) && editedValidTo.isBefore(dbVersion.getValidTo())) {
                return true;
              }
              // match 1 or more dbVersion/s between edited version
              return dbVersion.getValidFrom().isAfter(editedValidFrom) && dbVersion.getValidTo()
                  .isBefore(editedValidTo);
            }
        )
        .findFirst();

    if (currentVersionMatch.isEmpty()) {
      // match edited version after last dbVersion
      if (editedValidFrom.isAfter(dbVersions.getLast().getValidTo())) {
        return dbVersions.getLast();
      }
      // match edited version before first dbVersion
      if (editedValidTo.isBefore(dbVersions.getFirst().getValidFrom())) {
        return dbVersions.getFirst();
      }
    }

    return currentVersionMatch.orElseThrow(() -> new RuntimeException("Not found current point version"));
  }

  public <T extends Versionable> List<T> findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<T> versions) {
    List<T> collected = versions.stream()
        .filter(toVersioning -> !toVersioning.getValidFrom().isAfter(editedValidTo))
        .filter(toVersioning -> !toVersioning.getValidTo().isBefore(editedValidFrom))
        .toList();
    if (!collected.isEmpty() &&
        (collected.getFirst().getValidFrom().equals(editedValidFrom) && collected.getLast().getValidTo()
            .equals(editedValidTo))) {
      return collected;
    }
    return Collections.emptyList();
  }

}
