package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.api.SectorGroupApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.service.SectorGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorGroupApiInternalController implements SectorGroupApiInternal {

  private final SectorGroupService sectorGroupService;

  @Override
  public Container<SectorGroupVersionModel> getSectorGroupsOfTrafficPoint(String trafficPointSloid, Pageable pageable) {
    return sectorGroupService.getSectorGroupsOfTrafficPoint(trafficPointSloid, pageable);
  }

}
