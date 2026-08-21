package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
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
public class SectorGroupBulkImportService {

  private final SectorGroupApiClient sectorGroupApiClient;

  @RunAsUser
  public void createSectorGroupByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<SectorGroupCreateCsvModel> bulkImportContainer) {
    log.info("Create versions in name of the user: {}", userName);
    createSectorGroup(bulkImportContainer);
  }

  public void createSectorGroup(BulkImportUpdateContainer<SectorGroupCreateCsvModel> bulkImportContainer) {
    CreateSectorGroupVersionModel createModel = SectorGroupBulkImportCreate.apply(bulkImportContainer);
    sectorGroupApiClient.createSectorGroupVersion(createModel);
  }

}
