package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.UpdateSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorGroupApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.service.sector.SectorGroupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorGroupController implements SectorGroupApiV1 {

  private final SectorGroupService sectorGroupService;

  @Override
  public List<SectorGroupVersionModel> getSectorGroups() {
    return sectorGroupService.getSectorGroups();
  }

  @Override
  public List<SectorGroupVersionModel> getSectorGroup(String sloid) {
    return sectorGroupService.getSectorGroup(sloid);
  }

  @Override
  public ReadSectorGroupVersionModel getSectorGroupVersion(Long id) {
    return sectorGroupService.getSectorGroupVersion(id);
  }

  @Override
  public ReadSectorGroupVersionModel createSectorGroupVersion(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    return sectorGroupService.createSectorGroup(createSectorGroupVersionModel);
  }

  @Override
  public List<SectorGroupVersionModel> updateSectorGroupVersion(Long id,
      UpdateSectorGroupVersionModel updateSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersionToUpdate = sectorGroupService.getSectorGroupVersionById(id);

    SectorGroupVersion editedVersion = SectorGroupMapper.toEntity(updateSectorGroupVersionModel);
    sectorGroupService.updateSectorGroup(sectorGroupVersionToUpdate, editedVersion);
    List<SectorGroupVersion> updatedSectorGroup = sectorGroupService.findAllBySloidOrderByValidFrom(
        sectorGroupVersionToUpdate.getSloid());

    return updatedSectorGroup
        .stream()
        .map(SectorGroupMapper::toModel)
        .toList();
  }
}
