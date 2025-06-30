package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorGroupApiV1;
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
  public List<ReadSectorGroupVersionModel> getSectorVersions() {
    return sectorGroupService.getSectorGroups().stream().map(SectorGroupMapper::toModel).toList();
  }

  @Override
  public ReadSectorGroupVersionModel createSectorVersion(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    return sectorGroupService.createSectorGroup(createSectorGroupVersionModel);
  }

  @Override
  public ReadSectorGroupVersionModel updateSectorVersion() {
    return null;
  }
}
