package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SectorGroupService {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final ServicePointService servicePointService;

  public List<SectorGroupVersion> getSectorGroups() {
    return sectorGroupVersionRepository.findAll();
  }

  public ReadSectorGroupVersionModel createSectorGroup(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersion = SectorGroupMapper.toEntity(createSectorGroupVersionModel);
    //TODO simplify create method.
    if (trafficPointElementService.findBySloidOrderByValidFrom(createSectorGroupVersionModel.getTrafficPointSloid()).isEmpty()) {
      throw new RuntimeException("Traffic point not found");
    }

    List<SectorVersion> sectorVersions =
        sectorVersionRepository.findAllBySloidIn(createSectorGroupVersionModel.getSectorSloids());

    if (sectorVersions.isEmpty()) {
      throw new RuntimeException("No sector version found");
    }

    sectorVersions.forEach(sectorVersion -> {
      if (sectorVersion.getTrafficPointSloid().equals(createSectorGroupVersionModel.getTrafficPointSloid())) {
        sectorGroupVersion.getSectorVersions().add(sectorVersion);
      } else {
        throw new RuntimeException("Traffic Point sloid of sector not matching with sector group traffic point sloid");
      }
    });

    SectorGroupVersion saved = sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion);

    //TODO Versioning

    //TODO add PreAuhtorize

    return SectorGroupMapper.toModel(saved);
  }

}
