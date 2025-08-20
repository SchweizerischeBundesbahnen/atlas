package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorApiInternal;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.service.sector.SectorService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorApiInternalController implements SectorApiInternal {

  private final SectorService sectorService;
  private final TrafficPointElementService trafficPointElementService;
  private final ServicePointService servicePointService;

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
    SectorVersion sectorVersion = SectorMapper.toEntity(createSectorVersionModel);
    trafficPointElementService.doesTrafficPointExist(sectorVersion.getTrafficPointSloid());
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementService.findBySloidOrderByValidFrom(sectorVersion.getTrafficPointSloid()).getFirst();

    List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
        trafficPointElementVersion.getServicePointNumber());

    return sectorService.create(sectorVersion, servicePointVersions);
  }

  @Override
  public List<SectorVersionModel> updateSectorVersion(Long id, SectorVersionModel updateSectorVersionModel) {
    SectorVersion sectorVersionToUpdate = sectorService.getSectorVersionById(id);
    SectorVersion editedVersion = SectorMapper.toEntity(updateSectorVersionModel);

    trafficPointElementService.doesTrafficPointExist(sectorVersionToUpdate.getTrafficPointSloid());
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementService.findBySloidOrderByValidFrom(sectorVersionToUpdate.getTrafficPointSloid()).getFirst();

    List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
        trafficPointElementVersion.getServicePointNumber());

    sectorService.update(sectorVersionToUpdate, editedVersion, servicePointVersions);

    List<SectorVersion> updatedSector = sectorService.findAllBySloidOrderByValidFrom(
        sectorVersionToUpdate.getSloid());

    return updatedSector
        .stream()
        .map(SectorMapper::toModel)
        .toList();
  }
}
