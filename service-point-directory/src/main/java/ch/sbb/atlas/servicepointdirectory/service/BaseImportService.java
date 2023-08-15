package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.servicepointdirectory.entity.BasePointVersion;
import ch.sbb.atlas.versioning.model.Versionable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseImportService<T extends BasePointVersion & Versionable> {

  protected abstract void save(T element);

  protected abstract void copyPropertiesFromCsvVersionToDbVersion(T csvVersion, T dbVersion);

  protected abstract ItemImportResult addInfoToItemImportResult(
      ItemImportResultBuilder itemImportResultBuilder,
      T element
  );

  /**
   * In case we want to merge 2 or more versions from a CSV File (Import or "Massen Import") first we need to compare the
   * number of the found DB versions with the number of the versions present in the CSV File.
   * If the number of the CSV File versions are less than the DB Versions, and we found more than one version
   * exactly included between CSV Version validFrom and CSV Version validTo, than we replace the versions properties,
   * (expect validFrom, validTo and id) and save the versions that comes from the DB.
   * ---
   * Difference between versioning merge and this merge:
   * Case it works with normal versioning:
   * DB:    |- v1,1.1.2020-31.12.2020,value:A -|  |- v2,1.1.2022-31.12.2022,value:B -|
   * CSV:   |--------------------- v1,1.1.2020-31.12.2022,value:B -------------------|
   * Case it needs this pre-check:
   * DB:    |- v1,1.1.2020-31.12.2020,value:A -|  |- v2,1.1.2022-31.12.2022,value:B -|
   * CSV:   |--------------------- v1,1.1.2020-31.12.2022,value:A -------------------|
   * The order from the value difference is decisive.
   */
  public void replaceCsvMergedVersions(List<T> dbVersions, List<T> csvVersions) {
    if (dbVersions.size() > csvVersions.size()) {
      log.info(
          "The CSV versions are less than the versions stored in the DB. A merge may have taken place...");
      for (T csvVersion : csvVersions) {
        List<T> dbVersionsFoundToBeReplaced =
            BasePointUtility.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(csvVersion.getValidFrom(),
                csvVersion.getValidTo(),
                dbVersions);
        if (!dbVersionsFoundToBeReplaced.isEmpty()) {
          updateMergedVersions(csvVersion, dbVersionsFoundToBeReplaced);
        }
      }
    }
  }

  protected ItemImportResult buildSuccessImportResult(T element) {
    ItemImportResultBuilder successResultBuilder = ItemImportResult.successResultBuilder();
    return addInfoToItemImportResult(successResultBuilder, element);
  }

  protected ItemImportResult buildFailedImportResult(T element, Exception exception) {
    ItemImportResultBuilder failedResultBuilder = ItemImportResult.failedResultBuilder(exception);
    return addInfoToItemImportResult(failedResultBuilder, element);
  }

  private void updateMergedVersions(T csvVersion, List<T> dbVersionsFoundToBeReplaced) {
    log.info("The properties of the following versions: {}", dbVersionsFoundToBeReplaced);
    for (T dbVersion : dbVersionsFoundToBeReplaced) {
      log.info("will be overridden with (expect [validFrom, validTo, id]): {}", dbVersion);
      copyPropertiesFromCsvVersionToDbVersion(csvVersion, dbVersion);
      save(dbVersion);
    }
  }

}
