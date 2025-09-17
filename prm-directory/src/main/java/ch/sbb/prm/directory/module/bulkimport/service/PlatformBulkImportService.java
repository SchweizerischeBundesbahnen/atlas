package ch.sbb.prm.directory.module.bulkimport.service;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.prm.directory.exception.BulkPlatformUpdateValidationException;
import ch.sbb.prm.directory.module.bulkimport.client.PlatformApiClient;
import ch.sbb.prm.directory.module.bulkimport.mapper.PlatformBulkImportCompleteUpdate;
import ch.sbb.prm.directory.module.bulkimport.mapper.PlatformBulkImportReducedUpdate;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import ch.sbb.prm.directory.util.PrmMeansOfTransportHelper;
import java.util.List;
import java.util.Set;
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

  private final StopPointService stopPointService;
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

    List<PlatformVersion> currentPlatformVersions = getCurrentPlatformVersionsReduced(platformReducedUpdateCsvModel);
    PlatformVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        platformReducedUpdateCsvModel.getValidFrom(), platformReducedUpdateCsvModel.getValidTo());

    isPlatformReduced(currentVersion);

    PlatformVersionModel updateModel = PlatformBulkImportReducedUpdate.apply(bulkImportUpdateContainer, currentVersion);

    platformApiClient.updatePlatform(currentVersion.getId(), updateModel);
  }

  @RunAsUser
  public void updatePlatformCompleteByUsername(@RunAsUserParameter String username,
      BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", username);
    updatePlatformComplete(bulkImportContainer);
  }

  public void updatePlatformComplete(BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel> bulkImportUpdateContainer) {
    PlatformCompleteUpdateCsvModel platformCompleteUpdateCsvModel = bulkImportUpdateContainer.getObject();

    List<PlatformVersion> currentPlatformVersions = getCurrentPlatformVersionsComplete(platformCompleteUpdateCsvModel);

    PlatformVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        platformCompleteUpdateCsvModel.getValidFrom(), platformCompleteUpdateCsvModel.getValidTo());

    isPlatformComplete(currentVersion);

    PlatformVersionModel updateModel = PlatformBulkImportCompleteUpdate.apply(bulkImportUpdateContainer, currentVersion);

    platformApiClient.updatePlatform(currentVersion.getId(), updateModel);
  }

  private List<PlatformVersion> getCurrentPlatformVersionsReduced(PlatformReducedUpdateCsvModel platformReducedUpdateCsvModel) {
    if (platformReducedUpdateCsvModel.getSloid() != null) {
      List<PlatformVersion> platformVersions = platformService.getAllVersions(platformReducedUpdateCsvModel.getSloid());
      if (platformVersions.isEmpty()) {
        throw new SloidNotFoundException(platformReducedUpdateCsvModel.getSloid());
      }
      return platformVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

  private List<PlatformVersion> getCurrentPlatformVersionsComplete(
      PlatformCompleteUpdateCsvModel platformCompleteUpdateCsvModel) {
    if (platformCompleteUpdateCsvModel.getSloid() != null) {
      List<PlatformVersion> platformVersions = platformService.getAllVersions(platformCompleteUpdateCsvModel.getSloid());
      if (platformVersions.isEmpty()) {
        throw new SloidNotFoundException(platformCompleteUpdateCsvModel.getSloid());
      }
      return platformVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

  private void isPlatformComplete(PlatformVersion currentVersion) {
    Set<MeanOfTransport> meanOfTransports =
        stopPointService.getMeansOfTransportOfAllVersions(currentVersion.getParentServicePointSloid());

    boolean isReduced = PrmMeansOfTransportHelper.isReduced(meanOfTransports);

    if (isReduced) {
      throw new BulkPlatformUpdateValidationException(true, currentVersion.getSloid());
    }
  }

  private void isPlatformReduced(PlatformVersion currentVersion) {
    Set<MeanOfTransport> meanOfTransports =
        stopPointService.getMeansOfTransportOfAllVersions(currentVersion.getParentServicePointSloid());

    boolean isReduced = PrmMeansOfTransportHelper.isReduced(meanOfTransports);

    if (!isReduced) {
      throw new BulkPlatformUpdateValidationException(false, currentVersion.getSloid());
    }
  }
}
