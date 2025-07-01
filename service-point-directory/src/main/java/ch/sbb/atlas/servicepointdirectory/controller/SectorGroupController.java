package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorGroupApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.service.SectorGroupService;
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
  public List<ReadSectorGroupVersionModel> getSectorGroupVersions() {
    return sectorGroupService.getSectorGroups().stream()
        .map(SectorGroupMapper::toReadModelWithSectors)
        .toList();
  }

  @Override
  public ReadSectorGroupVersionModel createSectorGroupVersion(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    return sectorGroupService.createSectorGroup(createSectorGroupVersionModel);
  }

  @Override
  public List<ReadSectorGroupVersionModel> updateSectorGroupVersion(Long id,
      UpdateSectorGroupVersionModel updateSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersionToUpdate = sectorGroupService.getSectorGroupVersionById(id);

    SectorGroupVersion editedVersion = SectorGroupMapper.toEntity(updateSectorGroupVersionModel);
    sectorGroupService.updateSectorGroup(sectorGroupVersionToUpdate, editedVersion);
    List<SectorGroupVersion> updatedSectorGroup = sectorGroupService.findAllBySloidOrderByValidFrom(
        sectorGroupVersionToUpdate.getSloid());

    return updatedSectorGroup
        .stream()
        .map(SectorGroupMapper::toReadModel)
        .toList();
  }
}
