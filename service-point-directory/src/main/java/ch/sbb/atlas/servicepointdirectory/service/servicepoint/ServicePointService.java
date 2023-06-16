package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServicePointService {

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final VersionableService versionableService;
  private final ServicePointValidationService servicePointValidationService;

  public Page<ServicePointVersion> findAll(ServicePointSearchRestrictions servicePointSearchRestrictions) {
    return servicePointVersionRepository.loadByIdsFindBySpecification(servicePointSearchRestrictions.getSpecification(),
        servicePointSearchRestrictions.getPageable());
  }

  public List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  public boolean isServicePointNumberExisting(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.existsByNumber(servicePointNumber);
  }

  public Optional<ServicePointVersion> findById(Long id) {
    return servicePointVersionRepository.findById(id);
  }

  public void deleteById(Long id) {
    servicePointVersionRepository.deleteById(id);
    servicePointVersionRepository.flush();
  }

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#servicePointVersion, "
          + "T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public ServicePointVersion save(ServicePointVersion servicePointVersion) {
    servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion);
    return servicePointVersionRepository.saveAndFlush(servicePointVersion);
  }

  @PreAuthorize("@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(#editedVersion, "
          + "#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public ServicePointVersion updateServicePointVersion(ServicePointVersion currentVersion, ServicePointVersion editedVersion) {
    servicePointVersionRepository.incrementVersion(currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }

    List<ServicePointVersion> existingDbVersions = findAllByNumberOrderByValidFrom(currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, existingDbVersions);
    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        this::save, this::deleteById);
    return currentVersion;
  }

}
