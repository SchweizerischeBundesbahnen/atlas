package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.SectorVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.service.SectorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorController implements SectorApiV1 {

  private final SectorService sectorService;

  @Override
  public List<SectorVersionModel> getSectors() {
    return sectorService.getSectors();
  }

  @Override
  public List<SectorVersionModel> getSector(String sloid) {
    return sectorService.getSector(sloid);
  }

  @Override
  public SectorVersionModel getSectorVersion(Long id) {
    return SectorMapper.toModel(sectorService.getSectorVersionById(id));
  }

  @Override
  public SectorVersionModel createSectorVersion(SectorVersionModel createSectorVersionModel) {
    return sectorService.createSector(createSectorVersionModel);
  }

  @Override
  public List<SectorVersionModel> updateSectorVersion(Long id, UpdateSectorVersionModel updateSectorVersionModel) {
    SectorVersion sectorVersionToUpdate = sectorService.getSectorVersionById(id);
    SectorVersion editedVersion = SectorMapper.toEntity(updateSectorVersionModel);
    sectorService.updateSector(sectorVersionToUpdate, editedVersion);

    List<SectorVersion> updatedSector = sectorService.findAllBySloidOrderByValidFrom(
        sectorVersionToUpdate.getSloid());

    return updatedSector
        .stream()
        .map(SectorMapper::toModel)
        .toList();
  }
}
