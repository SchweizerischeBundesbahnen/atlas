package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepointdirectory.entity.BasePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BaseImportService {

  /**
   * In case we want to merge 2 or more versions from a CSV File (Import or "Massen Import") first we need to compare the
   * number of the found DB versions with the number of the versions present in the CSV File.
   * If the number of the CSV File versions are less than the DB Versions, and we found more than one version
   * exactly included between CSV Version validFrom and CSV Version validTo, than we replace the versions properties,
   * (expect validFrom, validTo and id) and save the versions that comes from the DB.
   */
  public static <T extends BasePointVersion & Versionable> void replaceCsvMergedVersions(List<T> dbVersions, List<T> csvVersions,
      Consumer<T> saveReference) {
    if (dbVersions.size() > csvVersions.size()) {
      log.info(
          "The ServicePoint CSV versions are less than the ServicePoint versions stored in the DB. A merge may have taken place"
              + "...");
      for (T csvVersion : csvVersions) {
        List<T> dbVersionsFoundToBeReplaced =
            findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(csvVersion.getValidFrom(), csvVersion.getValidTo(),
                dbVersions);
        if (dbVersionsFoundToBeReplaced.size() > 1) {
          updateMergedVersions(csvVersion, dbVersionsFoundToBeReplaced, saveReference);
        }
      }
    }
  }

  public static <T extends Versionable> List<T> findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
      LocalDate editedValidFrom, LocalDate editedValidTo, List<T> versions) {
    List<T> collected = versions.stream()
        .filter(toVersioning -> !toVersioning.getValidFrom().isAfter(editedValidTo))
        .filter(toVersioning -> !toVersioning.getValidTo().isBefore(editedValidFrom))
        .collect(Collectors.toList());
    if (!collected.isEmpty() &&
        (collected.get(0).getValidFrom().equals(editedValidFrom) && collected.get(collected.size() - 1).getValidTo()
            .equals(editedValidTo))) {
      return collected;
    }
    return List.of();
  }

  private static <T extends BasePointVersion> void updateMergedVersions(T csvVersion, List<T> dbVersionsFoundToBeReplaced,
      Consumer<T> saveReference) {
    log.info("The properties of the following versions: {}", dbVersionsFoundToBeReplaced);
    for (T dbVersion : dbVersionsFoundToBeReplaced) {
      log.info("will be overridden with (expect [validFrom, validTo, id]): {}", dbVersion);
      BeanCopyUtil.copyNonNullProperties(csvVersion, dbVersion,
          ServicePointVersion.Fields.validFrom,
          ServicePointVersion.Fields.validTo,
          ServicePointVersion.Fields.id
      );
      dbVersion.setThisAsParentOnRelatingEntities();
      saveReference.accept(dbVersion);
    }
  }

}
