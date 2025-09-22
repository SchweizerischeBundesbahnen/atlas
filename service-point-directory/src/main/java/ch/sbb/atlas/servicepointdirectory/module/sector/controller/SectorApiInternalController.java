package ch.sbb.atlas.servicepointdirectory.module.sector.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sector.api.SectorApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.sector.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.module.sector.service.SectorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorApiInternalController implements SectorApiInternal {

  private final SectorService sectorService;

  @Override
  public Container<ReadSectorVersionModel> getSectorsOfTrafficPoint(String trafficPointSloid, Pageable pageable) {
    return sectorService.getSectorsOfTrafficPoint(trafficPointSloid, pageable);
  }

  @Override
  public List<ReadSectorVersionModel> getSectorsOfTrafficPointValidToday(String trafficPointSloid) {
    return sectorService.getSectorsOfTrafficPointValidToday(trafficPointSloid).stream().map(SectorMapper::toModel).toList();
  }

}
