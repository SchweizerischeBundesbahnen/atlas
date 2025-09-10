package ch.sbb.atlas.servicepointdirectory.module.sector.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service.TrafficPointElementService;
import ch.sbb.atlas.servicepointdirectory.service.SectorValidationService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectorService {

  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementService trafficPointElementService;
  private final VersionableService versionableService;
  private final LocationService locationService;
  private final SectorValidationService sectorValidationService;

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @Transactional
  public SectorVersion create(SectorVersion sectorVersion,
      List<ServicePointVersion> servicePointVersions) {
    return createSector(sectorVersion, servicePointVersions);
  }

  SectorVersion createSector(SectorVersion sectorVersion,
      List<ServicePointVersion> servicePointVersions) {
    trafficPointElementService.doesTrafficPointExist(sectorVersion.getTrafficPointSloid());

    sectorValidationService.validateValidity(sectorVersion);
    sectorValidationService.validateMeanOfTransportOfServicePoint(servicePointVersions);

    sectorVersion.setSloid(locationService.generateSloid(SloidType.SECTOR, sectorVersion.getTrafficPointSloid()));

    return save(sectorVersion);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @Transactional
  public void update(SectorVersion currentVersion, SectorVersion editedVersion, List<ServicePointVersion> servicePointVersions) {
    updateSector(currentVersion, editedVersion);
  }

  void updateSector(SectorVersion currentVersion, SectorVersion editedVersion) {
    sectorVersionRepository.incrementVersion(currentVersion.getSloid());

    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointSloid(currentVersion.getTrafficPointSloid());

    sectorValidationService.validateValidity(editedVersion);

    List<SectorVersion> currentVersions = getSector(currentVersion.getSloid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion,
        currentVersions);

    versionableService.applyVersioning(SectorVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(sectorVersionRepository));
  }

  public Container<SectorVersionModel> getSectorsOfTrafficPoint(String trafficPointSloid, Pageable pageable) {
    List<SectorVersion> sectors = sectorVersionRepository.findAllByTrafficPointSloid(trafficPointSloid,
        pageable.getSort());

    List<SectorVersionModel> overviewModels = sectors.stream().map(SectorMapper::toModel).toList();
    List<SectorVersionModel> displayableModels = OverviewDisplayBuilder.mergeVersionsForDisplay(overviewModels,
        SectorVersionModel::getSloid);
    return OverviewDisplayBuilder.toPagedContainer(displayableModels, pageable);
  }

  public SectorVersion getSectorVersionById(Long id) {
    return sectorVersionRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<SectorVersion> getSector(String sectorSloid) {
    List<SectorVersion> sectorVersions = sectorVersionRepository.findAllBySloidOrderByValidFrom(sectorSloid);

    if (sectorVersions.isEmpty()) {
      throw new SloidNotFoundException(sectorSloid);
    }

    return sectorVersions;
  }

  private SectorVersion save(SectorVersion sectorVersion) {
    sectorVersion.setStatus(Status.VALIDATED);
    return sectorVersionRepository.saveAndFlush(sectorVersion);
  }

}
