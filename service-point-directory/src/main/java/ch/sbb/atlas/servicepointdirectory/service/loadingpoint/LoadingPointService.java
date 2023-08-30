package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.LoadingPointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Getter
@RequiredArgsConstructor
public class LoadingPointService {

  private final VersionableService versionableService;
  private final LoadingPointVersionRepository loadingPointVersionRepository;
  private final CrossValidationService crossValidationService;

  public Page<LoadingPointVersion> findAll(LoadingPointSearchRestrictions searchRestrictions) {
    return loadingPointVersionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public List<LoadingPointVersion> findLoadingPoint(ServicePointNumber servicePointNumber, Integer loadingPointNumber) {
    return loadingPointVersionRepository.findAllByServicePointNumberAndNumberOrderByValidFrom(servicePointNumber,
        loadingPointNumber);
  }

  public Optional<LoadingPointVersion> findById(Long id) {
    return loadingPointVersionRepository.findById(id);
  }

  public boolean isLoadingPointExisting(ServicePointNumber servicePointNumber, Integer loadingPointNumber) {
    return loadingPointVersionRepository.existsByServicePointNumberAndNumber(servicePointNumber, loadingPointNumber);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject"
          + "(#associatedServicePoint, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public LoadingPointVersion create(LoadingPointVersion loadingPointVersion, List<ServicePointVersion> associatedServicePoint) {
    if (isLoadingPointExisting(loadingPointVersion.getServicePointNumber(), loadingPointVersion.getNumber())) {
      throw new LoadingPointNumberAlreadyExistsException(loadingPointVersion.getServicePointNumber(),
          loadingPointVersion.getNumber());
    }
    return save(loadingPointVersion);
  }

  public LoadingPointVersion save(LoadingPointVersion loadingPointVersion) {
    crossValidationService.validateServicePointNumberExists(loadingPointVersion.getServicePointNumber());
    return loadingPointVersionRepository.saveAndFlush(loadingPointVersion);
  }

  @PreAuthorize(
      "@countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreateOrEditServicePointDependentObject"
          + "(#associatedServicePoint, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)")
  public void updateVersion(LoadingPointVersion currentVersion, LoadingPointVersion editedVersion,
      List<ServicePointVersion> associatedServicePoint) {
    loadingPointVersionRepository.incrementVersion(currentVersion.getServicePointNumber(), currentVersion.getNumber());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
        .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(LoadingPointVersion.class.getSimpleName(), "version");
    }

    List<LoadingPointVersion> currentVersions = findLoadingPoint(currentVersion.getServicePointNumber(),
        currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion, editedVersion, currentVersions);

    versionableService.applyVersioning(LoadingPointVersion.class, versionedObjects, this::save,
        loadingPointVersionRepository::deleteById);
  }

}
