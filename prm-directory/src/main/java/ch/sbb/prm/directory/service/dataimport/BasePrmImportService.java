package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.BaseImportService;
import ch.sbb.atlas.imports.util.BeanCopyUtil;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.prm.directory.entity.StopPointVersion;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePrmImportService<T extends Versionable> extends BaseImportService<T> {

  /**
   * In case we want to merge 2 or more versions from a CSV File (Import or "Massen Import") first we need to compare the number
   * of the found DB versions with the number of the versions present in the CSV File. If the number of the CSV File versions are
   * less than the DB Versions, and we found more than one version exactly included between CSV Version validFrom and CSV Version
   * validTo, than we replace the versions properties, (expect validFrom, validTo and id) and save the versions that comes from
   * the DB.
   * --- Difference between versioning merge and this merge ---
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
            ImportUtils.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(csvVersion.getValidFrom(),
                csvVersion.getValidTo(),
                dbVersions);
        if (!dbVersionsFoundToBeReplaced.isEmpty()) {
          updateMergedVersions(csvVersion, dbVersionsFoundToBeReplaced);
        }
      }
    }
  }

  private void updateMergedVersions(T csvVersion, List<T> dbVersionsFoundToBeReplaced) {
    log.info("The properties of the following versions: {}", dbVersionsFoundToBeReplaced);
    for (T dbVersion : dbVersionsFoundToBeReplaced) {
      log.info("will be overridden with (expect [validFrom, validTo, id]): {}", dbVersion);
      BeanCopyUtil.copyNonNullProperties(csvVersion, dbVersion, StopPointVersion.Fields.validFrom,
          StopPointVersion.Fields.validTo,
          StopPointVersion.Fields.id);
      save(dbVersion);
    }
  }

}
