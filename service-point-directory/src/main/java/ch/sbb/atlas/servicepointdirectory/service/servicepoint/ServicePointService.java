package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSearchVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServicePointService {

  private static final int SEARCH_RESULT_SIZE = 200;

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final VersionableService versionableService;
  private final ServicePointValidationService servicePointValidationService;
  private final ServicePointSearchVersionRepository servicePointSearchVersionRepository;
  private final ServicePointTerminationService servicePointTerminationService;

  public List<ServicePointSearchResult> searchServicePointVersion(String value) {
    List<ServicePointSearchResult> servicePointSearchResults = servicePointSearchVersionRepository.searchServicePoints(value);
    return getSearchResults(servicePointSearchResults);
  }

  public List<ServicePointSearchResult> searchSwissOnlyServicePointVersion(String value) {
    List<ServicePointSearchResult> servicePointSearchResults =
        servicePointSearchVersionRepository.searchSwissOnlyStopPointServicePoints(value);
    return getSearchResults(servicePointSearchResults);
  }

  public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(String value) {
    List<ServicePointSearchResult> servicePointSearchResults =
        servicePointSearchVersionRepository.searchServicePointsWithRouteNetworkTrue(
        value);
    return getSearchResults(servicePointSearchResults);
  }


  public Page<ServicePointVersion> findAll(ServicePointSearchRestrictions servicePointSearchRestrictions) {
    return servicePointVersionRepository.loadByIdsFindBySpecification(servicePointSearchRestrictions.getSpecification(),
        servicePointSearchRestrictions.getPageable());
  }

  public List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  public List<ServicePointVersion> findBySloidAndOrderByValidFrom(String sloid){
    return servicePointVersionRepository.findBySloidOrderByValidFrom(sloid);
  }

  public List<ServicePointVersion> findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(
      ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(servicePointNumber);
  }

  public boolean isServicePointNumberExisting(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.existsByNumber(servicePointNumber);
  }

  public Optional<ServicePointVersion> findById(Long id) {
    return servicePointVersionRepository.findById(id);
  }

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#servicePointVersion, "
      + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public ServicePointVersion save(ServicePointVersion servicePointVersion) {
    servicePointVersion.setStatus(Status.VALIDATED);
    servicePointVersion.setEditionDate(LocalDateTime.now());
    servicePointVersion.setEditor(UserService.getUserIdentifier());

    servicePointValidationService.validateAndSetAbbreviation(servicePointVersion);
    servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion);
    return servicePointVersionRepository.saveAndFlush(servicePointVersion);
  }

  public ServicePointVersion saveWithoutValidationForImportOnly(ServicePointVersion servicePointVersion) {
    servicePointVersion.setStatus(Status.VALIDATED);
    return servicePointVersionRepository.saveAndFlush(servicePointVersion);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(#editedVersion, "
          + "#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public void update(ServicePointVersion currentVersion, ServicePointVersion editedVersion,
      List<ServicePointVersion> currentVersions) {
    updateServicePointVersion(currentVersion, editedVersion, currentVersions);
  }

  public ServicePointVersion updateServicePointVersion(ServicePointVersion currentVersion, ServicePointVersion editedVersion,
      List<ServicePointVersion> currentVersions) {
    servicePointVersionRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }
    editedVersion.setNumber(currentVersion.getNumber());
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setStatusDidok3(currentVersion.getStatusDidok3());
    editedVersion.setNumberShort(currentVersion.getNumberShort());
    editedVersion.setCountry(currentVersion.getCountry());

    List<ServicePointVersion> existingDbVersions = findAllByNumberOrderByValidFrom(currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);

    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        this::save, new ApplyVersioningDeleteByIdLongConsumer(servicePointVersionRepository));

    List<ServicePointVersion> afterUpdateServicePoint = findAllByNumberOrderByValidFrom(currentVersion.getNumber());
    servicePointTerminationService.checkTerminationAllowed(currentVersions, afterUpdateServicePoint);
    return currentVersion;
  }

  private List<ServicePointSearchResult> getSearchResults(List<ServicePointSearchResult> servicePointSearchResults) {
    if (servicePointSearchResults.size() > SEARCH_RESULT_SIZE) {
      return servicePointSearchResults.subList(0, SEARCH_RESULT_SIZE);
    }
    return servicePointSearchResults;
  }

}
