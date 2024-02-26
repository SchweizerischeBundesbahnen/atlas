package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.service.OverviewService;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoAdminHeightResponse;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Slf4j
@Transactional
public class TrafficPointElementService {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;
  private final VersionableService versionableService;
  private final TrafficPointElementValidationService trafficPointElementValidationService;
  private final GeoReferenceService geoReferenceService;
  private final LocationService locationService;

  public TrafficPointElementService(TrafficPointElementVersionRepository trafficPointElementVersionRepository,
      VersionableService versionableService, TrafficPointElementValidationService trafficPointElementValidationService,
      GeoReferenceService geoReferenceService,
      LocationService locationService) {
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
    this.versionableService = versionableService;
    this.geoReferenceService = geoReferenceService;
    this.trafficPointElementValidationService = trafficPointElementValidationService;
    this.locationService = locationService;
  }

  public Page<TrafficPointElementVersion> findAll(TrafficPointElementSearchRestrictions searchRestrictions) {
    return trafficPointElementVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<TrafficPointElementVersion> findBySloidOrderByValidFrom(String sloid) {
    return trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  public Optional<TrafficPointElementVersion> findById(Long id) {
    return trafficPointElementVersionRepository.findById(id);
  }

  public boolean isTrafficPointElementExisting(String sloid) {
    return trafficPointElementVersionRepository.existsBySloid(sloid);
  }

  public void createThroughImport(TrafficPointElementVersion trafficPointElementVersion) {
    create(trafficPointElementVersion, null);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#servicePointVersions,T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public TrafficPointElementVersion create(TrafficPointElementVersion trafficPointElementVersion,
      List<ServicePointVersion> servicePointVersions) {
    if (trafficPointElementVersion.getSloid() != null) {
      locationService.claimSloid(LocationService.getSloidType(trafficPointElementVersion.getTrafficPointElementType()),
          trafficPointElementVersion.getSloid());
    } else {
      trafficPointElementVersion.setSloid(
          locationService.generateTrafficPointSloid(trafficPointElementVersion.getTrafficPointElementType(),
              trafficPointElementVersion.getServicePointNumber())
      );
    }
    return trafficPointElementVersionRepository.saveAndFlush(trafficPointElementVersion);
  }

  public TrafficPointElementVersion save(TrafficPointElementVersion trafficPointElementVersion) {
    trafficPointElementValidationService.validatePreconditionBusinessRules(trafficPointElementVersion);

    trafficPointElementVersion.setEditionDate(LocalDateTime.now());
    trafficPointElementVersion.setEditor(UserService.getUserIdentifier());

    return trafficPointElementVersionRepository.saveAndFlush(trafficPointElementVersion);
  }

  public TrafficPointElementVersion saveForImport(TrafficPointElementVersion trafficPointElementVersion) {
    trafficPointElementValidationService.validatePreconditionBusinessRules(trafficPointElementVersion);
    return trafficPointElementVersionRepository.saveAndFlush(trafficPointElementVersion);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject
      (#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public void update(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion,
      List<ServicePointVersion> currentVersions) {
    updateTrafficPointElementVersion(currentVersion, editedVersion);
  }

  public void updateTrafficPointElementVersion(TrafficPointElementVersion currentVersion,
      TrafficPointElementVersion editedVersion) {
    trafficPointElementVersionRepository.incrementVersion(currentVersion.getSloid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    editedVersion.setServicePointNumber(currentVersion.getServicePointNumber());
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setTrafficPointElementType(currentVersion.getTrafficPointElementType());

    List<TrafficPointElementVersion> dbVersions = findBySloidOrderByValidFrom(currentVersion.getSloid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion,
        dbVersions);
    versionableService.applyVersioning(TrafficPointElementVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(trafficPointElementVersionRepository));
  }

  public Container<ReadTrafficPointElementVersionModel> getTrafficPointElementsByServicePointNumber(
      Integer servicePointNumber, Pageable pageable, TrafficPointElementType trafficPointElementType) {

    TrafficPointElementSearchRestrictions trafficPointElementSearchRestrictions = TrafficPointElementSearchRestrictions.builder()
        .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
            .trafficPointElementType(trafficPointElementType)
            .servicePointNumbers(List.of(String.valueOf(servicePointNumber)))
            .build())
        .build();

    List<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementVersionRepository.findAll(
        trafficPointElementSearchRestrictions.getSpecification(), pageable.getSort());

    List<ReadTrafficPointElementVersionModel> trafficPointElements = trafficPointElementVersions.stream()
        .map(TrafficPointElementVersionMapper::toModel).toList();

    List<ReadTrafficPointElementVersionModel> displayableVersions = OverviewService.mergeVersionsForDisplay(trafficPointElements,
        ReadTrafficPointElementVersionModel::getSloid);
    return OverviewService.toPagedContainer(displayableVersions, pageable);
  }

  public List<TrafficPointElementVersion> getTrafficPointElementsByServicePointNumber(Integer servicePointNumber,
      LocalDate validOn) {
    return trafficPointElementVersionRepository.findAll(TrafficPointElementSearchRestrictions.builder()
        .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
            .servicePointNumbers(List.of(String.valueOf(servicePointNumber)))
            .validOn(validOn)
            .build())
        .build().getSpecification());
  }

  public void setHeightForTrafficPoints(TrafficPointElementVersion trafficPointElementVersion) {
    TrafficPointElementGeolocation trafficPointElementGeolocation =
        trafficPointElementVersion.getTrafficPointElementGeolocation();
    if (trafficPointElementGeolocation != null && trafficPointElementGeolocation.getHeight() == null) {
      GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(
          trafficPointElementGeolocation.asCoordinatePair());
      trafficPointElementGeolocation.setHeight(geoAdminHeightResponse.getHeight());
    }
  }
}
