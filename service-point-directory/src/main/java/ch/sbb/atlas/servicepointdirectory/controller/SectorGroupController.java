package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.SectorGroupApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.service.sector.SectorGroupService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SectorGroupController implements SectorGroupApiV1 {

  private final SectorGroupService sectorGroupService;
  private final TrafficPointElementService trafficPointElementService;
  private final ServicePointService servicePointService;

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
    SectorGroupVersion sectorGroupVersionToCreate = SectorGroupMapper.toEntity(createSectorGroupVersionModel);
    List<String> sectorSloidsToAdd = createSectorGroupVersionModel.getSectorSloids().stream().toList();

    trafficPointElementService.doesTrafficPointExist(createSectorGroupVersionModel.getTrafficPointSloid());
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementService.findBySloidOrderByValidFrom(createSectorGroupVersionModel.getTrafficPointSloid()).getFirst();

    List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
        trafficPointElementVersion.getServicePointNumber());

    return sectorGroupService.create(sectorGroupVersionToCreate, sectorSloidsToAdd, servicePointVersions);
  }

  @Override
  public List<SectorGroupVersionModel> updateSectorGroupVersion(Long id,
      SectorGroupVersionModel updateSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersionToUpdate = sectorGroupService.getSectorGroupVersionById(id);
    SectorGroupVersion editedVersion = SectorGroupMapper.toEntity(updateSectorGroupVersionModel);

    trafficPointElementService.doesTrafficPointExist(sectorGroupVersionToUpdate.getTrafficPointSloid());
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementService.findBySloidOrderByValidFrom(sectorGroupVersionToUpdate.getTrafficPointSloid()).getFirst();

    List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
        trafficPointElementVersion.getServicePointNumber());

    sectorGroupService.update(sectorGroupVersionToUpdate, editedVersion, servicePointVersions);
    List<SectorGroupVersion> updatedSectorGroup = sectorGroupService.findAllBySloidOrderByValidFrom(
        sectorGroupVersionToUpdate.getSloid());

    return updatedSectorGroup
        .stream()
        .map(SectorGroupMapper::toModel)
        .toList();
  }
}
