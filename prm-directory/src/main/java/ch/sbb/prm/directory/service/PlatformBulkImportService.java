package ch.sbb.prm.directory.service;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.PlatformReducedUpdateCsvModel;
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
  public void updatePlatformReducedByUsername(@RunAsUserParameter String username,
      BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", username);
    updatePlatformReduced(bulkImportContainer);
  }

  public void updatePlatformReduced(BulkImportUpdateContainer<PlatformReducedUpdateCsvModel> bulkImportUpdateContainer) {
    PlatformReducedUpdateCsvModel platformReducedUpdateCsvModel = bulkImportUpdateContainer.getObject();

    List<PlatformVersion> currentPlatformVersions = getCurrentPlatformVersions(platformReducedUpdateCsvModel);
    PlatformVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        platformReducedUpdateCsvModel.getValidFrom(), platformReducedUpdateCsvModel.getValidTo());
    PlatformVersion editedVersion = PlatformBulkImportUpdate.applyUpdateFromCsv(currentVersion, platformReducedUpdateCsvModel);
    PlatformBulkImportUpdate.applyNulling(bulkImportUpdateContainer.getAttributesToNull(), editedVersion);

    platformService.updatePlatformVersion(currentVersion, editedVersion);
  }

  private List<PlatformVersion> getCurrentPlatformVersions(PlatformReducedUpdateCsvModel platformReducedUpdateCsvModel) {
    if (platformReducedUpdateCsvModel.getSloid() != null) {
      List<PlatformVersion> platformVersions = platformService.getAllVersions(platformReducedUpdateCsvModel.getSloid());
      if (platformVersions.isEmpty()) {
        throw new SloidNotFoundException(platformReducedUpdateCsvModel.getSloid());
      }
      return platformVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

}
