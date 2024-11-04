package ch.sbb.prm.directory.service.bulk;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.service.PlatformService;
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
  private final PlatformApiClient platformApiClient;

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
    PlatformVersionModel updateModel = PlatformBulkImportUpdate.apply(bulkImportUpdateContainer, currentVersion);

    platformApiClient.updatePlatform(currentVersion.getId(), updateModel);
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
