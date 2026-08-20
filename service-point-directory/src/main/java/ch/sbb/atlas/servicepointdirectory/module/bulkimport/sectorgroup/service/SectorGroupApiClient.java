package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.api.SectorGroupApiV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SectorGroupApiClient {

  private final SectorGroupApiV1 sectorGroupApiV1;

  public void createSectorGroupVersion(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    sectorGroupApiV1.createSectorGroupVersion(createSectorGroupVersionModel);
  }

}
