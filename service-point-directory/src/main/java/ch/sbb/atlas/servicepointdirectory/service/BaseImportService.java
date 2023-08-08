package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.servicepointdirectory.entity.BasePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.versioning.model.Versionable;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseImportService<T extends BasePointVersion & Versionable> {

  /**
   * In case we want to merge 2 or more versions from a CSV File (Import or "Massen Import") first we need to compare the
   * number of the found DB versions with the number of the versions present in the CSV File.
   * If the number of the CSV File versions are less than the DB Versions, and we found more than one version
   * exactly included between CSV Version validFrom and CSV Version validTo, than we replace the versions properties,
   * (expect validFrom, validTo and id) and save the versions that comes from the DB.
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
        if (dbVersionsFoundToBeReplaced.size() > 1) {
          updateMergedVersions(csvVersion, dbVersionsFoundToBeReplaced);
        }
      }
    }
  }

  protected abstract void save(T element);

  private void updateMergedVersions(T csvVersion, List<T> dbVersionsFoundToBeReplaced) {
    log.info("The properties of the following versions: {}", dbVersionsFoundToBeReplaced);
    for (T dbVersion : dbVersionsFoundToBeReplaced) {
      log.info("will be overridden with (expect [validFrom, validTo, id]): {}", dbVersion);
      BeanCopyUtil.copyNonNullProperties(csvVersion, dbVersion,
          ServicePointVersion.Fields.validFrom,
          ServicePointVersion.Fields.validTo,
          ServicePointVersion.Fields.id
      );
      dbVersion.setThisAsParentOnRelatingEntities();
      save(dbVersion);
    }
  }

}
