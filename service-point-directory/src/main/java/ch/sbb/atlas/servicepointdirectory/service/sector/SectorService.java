package ch.sbb.atlas.servicepointdirectory.service.sector;

import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TrafficPointNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SectorService {

  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;

  public SectorService(SectorVersionRepository sectorVersionRepository, TrafficPointElementService trafficPointElementService,
      VersionableService versionableService) {
    this.sectorVersionRepository = sectorVersionRepository;
    this.trafficPointElementService = trafficPointElementService;
    this.versionableService = versionableService;
  }

  public List<SectorVersionModel> getSectors() {
    return sectorVersionRepository.findAll().stream().map(SectorMapper::toModel).toList();
  }

  public SectorVersionModel createSector(SectorVersionModel createSectorVersionModel) {
    SectorVersion sectorVersion = SectorMapper.toEntity(createSectorVersionModel);
    existTrafficPointElement(createSectorVersionModel.getTrafficPointSloid());
    //TODO locationService.claimSloid waiting for -> ATLAS-2963 (LocationService erweiterung)

    SectorVersion saved = save(sectorVersion);
    return SectorMapper.toModel(saved);
  }

  public void updateSector(SectorVersion currentVersion, SectorVersion editedVersion) {
    sectorVersionRepository.incrementVersion(currentVersion.getSloid());

    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointSloid(currentVersion.getTrafficPointSloid());

    List<SectorVersion> currentVersions = findAllBySloidOrderByValidFrom(currentVersion.getSloid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion,
        currentVersions);

    versionableService.applyVersioning(SectorVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(sectorVersionRepository));
  }

  public SectorVersion getSectorVersionById(Long id) {
    return sectorVersionRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<SectorVersionModel> getSector(String sectorSloid) {
    List<SectorVersion> sectorVersions = findAllBySloidOrderByValidFrom(sectorSloid);
    return sectorVersions.stream().map(SectorMapper::toModel).toList();
  }

  public List<SectorVersion> findAllBySloidOrderByValidFrom(String sectorSloid) {
    return sectorVersionRepository.findAllBySloidOrderByValidFrom(sectorSloid);
  }

  private SectorVersion save(SectorVersion sectorGroupVersion) {
    return sectorVersionRepository.saveAndFlush(sectorGroupVersion);
  }

  private void existTrafficPointElement(String sloid) {
    if (trafficPointElementService.findBySloidOrderByValidFrom(sloid).isEmpty()) {
      throw new TrafficPointNotFoundException(sloid);
    }
  }

}
