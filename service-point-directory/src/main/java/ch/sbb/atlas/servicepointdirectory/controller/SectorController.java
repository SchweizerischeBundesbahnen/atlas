package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorApiV1;
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
  public List<ReadSectorVersionModel> getSectorVersions() {
    return sectorService.getAllSectorVersions().stream().map(SectorMapper::toModel).toList();
  }

  @Override
  public ReadSectorVersionModel createSectorVersion(CreateSectorVersionModel createSectorVersionModel) {
    return sectorService.createSector(createSectorVersionModel);
  }
}
