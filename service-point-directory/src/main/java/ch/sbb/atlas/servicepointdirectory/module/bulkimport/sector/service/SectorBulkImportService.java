package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.service.SectorService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
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
public class SectorBulkImportService {

  private final SectorService sectorService;
  private final SectorApiClient sectorApiClient;

  @RunAsUser
  public void createSectorByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<SectorCreateCsvModel> bulkImportContainer) {
    log.info("Create versions in name of the user: {}", userName);
    createSector(bulkImportContainer);
  }

  public void createSector(BulkImportUpdateContainer<SectorCreateCsvModel> bulkImportContainer) {
    CreateSectorVersionModel createModel = SectorBulkImportCreate.apply(bulkImportContainer);
    sectorApiClient.createSectorVersion(createModel);
  }

  @RunAsUser
  public void updateSectorByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<SectorUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    updateSector(bulkImportContainer);
  }

  public void updateSector(BulkImportUpdateContainer<SectorUpdateCsvModel> bulkImportContainer) {
    SectorUpdateCsvModel sectorUpdateCsvModel = bulkImportContainer.getObject();

    List<SectorVersion> currentSectorVersions = getCurrentSectorVersions(sectorUpdateCsvModel);
    SectorVersion currentVersion = ImportUtils.getCurrentVersion(currentSectorVersions,
        sectorUpdateCsvModel.getValidFrom(), sectorUpdateCsvModel.getValidTo());

    CreateSectorVersionModel updateModel = SectorBulkImportUpdate.apply(bulkImportContainer, currentVersion);

    sectorApiClient.updateSectorVersion(currentVersion.getId(), updateModel);
  }

  private List<SectorVersion> getCurrentSectorVersions(SectorUpdateCsvModel sectorUpdateCsvModel) {
    if (sectorUpdateCsvModel.getSloid() == null) {
      throw new IllegalStateException("Sloid should be given");
    }
    return sectorService.findBySid4ptOrderByValidFrom(sectorUpdateCsvModel.getSloid());
  }

}
