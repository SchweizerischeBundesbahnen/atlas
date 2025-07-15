package ch.sbb.atlas.servicepointdirectory.service.sector;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SectorService {

  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;
  private final LocationService locationService;

  public SectorService(SectorVersionRepository sectorVersionRepository, TrafficPointElementService trafficPointElementService,
      VersionableService versionableService, LocationService locationService) {
    this.sectorVersionRepository = sectorVersionRepository;
    this.trafficPointElementService = trafficPointElementService;
    this.versionableService = versionableService;
    this.locationService = locationService;
  }

  public List<SectorVersionModel> getSectors() {
    return sectorVersionRepository.findAll().stream().map(SectorMapper::toModel).toList();
  }

  public SectorVersionModel createSector(SectorVersionModel createSectorVersionModel) {
    SectorVersion sectorVersion = SectorMapper.toEntity(createSectorVersionModel);
    trafficPointElementService.doesTrafficPointExist(createSectorVersionModel.getTrafficPointSloid());
    sectorVersion.setSloid(locationService.generateSloid(SloidType.SECTOR, createSectorVersionModel.getTrafficPointSloid()));
    SectorVersion saved = save(sectorVersion);
    return SectorMapper.toModel(saved);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @Transactional
  public SectorVersionModel create(SectorVersionModel createSectorVersionModel,
      List<ServicePointVersion> servicePointVersions) {
    return createSector(createSectorVersionModel);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @Transactional
  public void update(SectorVersion currentVersion, SectorVersion editedVersion, List<ServicePointVersion> servicePointVersions) {
    updateSector(currentVersion, editedVersion);
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

  private SectorVersion save(SectorVersion sectorVersion) {
    sectorVersion.setStatus(Status.VALIDATED);
    return sectorVersionRepository.saveAndFlush(sectorVersion);
  }

}
