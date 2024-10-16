package ch.sbb.prm.directory.service;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PlatformBulkImportService {

  private final PlatformService platformService;

  @RunAsUser
  public void updatePlatformByUsername(@RunAsUserParameter String username,
      BulkImportUpdateContainer<PlatformUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", username);
    updatePlatform(bulkImportContainer);
  }

  public void updatePlatform(BulkImportUpdateContainer<PlatformUpdateCsvModel> bulkImportUpdateContainer) {
    PlatformUpdateCsvModel platformUpdateCsvModel = bulkImportUpdateContainer.getObject();

    List<PlatformVersion> currentPlatformVersions = getCurrentPlatformVersions(platformUpdateCsvModel);
    PlatformVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        platformUpdateCsvModel.getValidFrom(), platformUpdateCsvModel.getValidTo());
    PlatformVersion editedVersion = PlatformBulkImportUpdate.applyUpdateFromCsv(currentVersion, platformUpdateCsvModel);
    PlatformBulkImportUpdate.applyNulling(bulkImportUpdateContainer.getAttributesToNull(), editedVersion);

    platformService.updatePlatformVersion(currentVersion, editedVersion);
  }

  private List<PlatformVersion> getCurrentPlatformVersions(PlatformUpdateCsvModel platformUpdateCsvModel) {
    if (platformUpdateCsvModel.getSloid() != null) {
      List<PlatformVersion> platformVersions = platformService.getAllVersions(platformUpdateCsvModel.getSloid());
      if (platformVersions.isEmpty()) {
        throw new SloidNotFoundException(platformUpdateCsvModel.getSloid());
      }
      return platformVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

}
