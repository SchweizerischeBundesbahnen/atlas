package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedWhenVersionInWrongStatusException;
import ch.sbb.atlas.servicepointdirectory.helper.TerminationHelper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSwissWithGeoTransfer;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.termination.TerminationCheck;
import ch.sbb.atlas.servicepointdirectory.termination.TerminationCheckParameter;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServicePointService {

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final VersionableService versionableService;
  private final ServicePointValidationService servicePointValidationService;
  private final ServicePointTerminationService servicePointTerminationService;
  private final ServicePointDistributor servicePointDistributor;

  public Page<ServicePointVersion> findAll(ServicePointSearchRestrictions servicePointSearchRestrictions) {
    return servicePointVersionRepository.loadByIdsFindBySpecification(servicePointSearchRestrictions.getSpecification(),
        servicePointSearchRestrictions.getPageable());
  }

  public List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointNumber);
  }

  public List<ServicePointVersion> findBySloidAndOrderByValidFrom(String sloid) {
    return servicePointVersionRepository.findBySloidOrderByValidFrom(sloid);
  }

  public boolean isServicePointNumberExisting(ServicePointNumber servicePointNumber) {
    return servicePointVersionRepository.existsByNumber(servicePointNumber);
  }

  public Optional<ServicePointVersion> findById(Long id) {
    return servicePointVersionRepository.findById(id);
  }

  public ServicePointVersion getServicePointVersionById(Long id) {
    return findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<ServicePointVersion> revokeServicePoint(ServicePointNumber servicePointNumber) {
    List<ServicePointVersion> servicePointVersions = servicePointVersionRepository.findAllByNumberOrderByValidFrom(
        servicePointNumber);
    boolean hasVersionInReview = servicePointVersions.stream()
        .anyMatch(servicePointVersion -> servicePointVersion.getStatus() == Status.IN_REVIEW);
    if (hasVersionInReview) {
      throw new TerminationNotAllowedWhenVersionInWrongStatusException(servicePointNumber, Status.IN_REVIEW);
    }
    servicePointVersions.forEach(servicePointVersion -> servicePointVersion.setStatus(Status.REVOKED));
    return servicePointVersions;
  }

  @TerminationCheck
  public ServicePointVersion validate(@TerminationCheckParameter ServicePointVersion servicePointVersion) {
    servicePointVersion.setStatus(Status.VALIDATED);
    return servicePointVersionRepository.saveAndFlush(servicePointVersion);
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#servicePointVersion,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public ServicePointVersion createAndPublish(ServicePointVersion servicePointVersion,
      Optional<ServicePointVersion> currentVersion,
      List<ServicePointVersion> currentVersions) {
    ServicePointVersion createdVersion = save(servicePointVersion, currentVersion, currentVersions);
    servicePointDistributor.publishServicePointsWithNumbers(createdVersion.getNumber());
    return createdVersion;
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(#editedVersion,
      #currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @TerminationCheck
  public List<ReadServicePointVersionModel> updateAndPublish(
      @TerminationCheckParameter ServicePointVersion servicePointVersionToUpdate,
      ServicePointVersion editedVersion, List<ServicePointVersion> currentVersions) {
    return updateAndPublishInternal(servicePointVersionToUpdate, editedVersion, currentVersions);
  }

  private List<ReadServicePointVersionModel> updateAndPublishInternal(ServicePointVersion servicePointVersionToUpdate,
      ServicePointVersion editedVersion, List<ServicePointVersion> currentVersions) {
    servicePointValidationService.checkIfServicePointStatusRevoked(servicePointVersionToUpdate);
    servicePointValidationService.checkIfServicePointStatusInReview(servicePointVersionToUpdate, editedVersion);
    servicePointValidationService.checkNotAffectingInReviewVersions(currentVersions, editedVersion);

    // Actual Update
    updateServicePointVersion(servicePointVersionToUpdate, editedVersion, currentVersions);

    // Publish to PRM
    List<ServicePointVersion> servicePoint = findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber());
    servicePointDistributor.publishServicePointsWithNumbers(servicePointVersionToUpdate.getNumber());

    return servicePoint
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
  }

  @TerminationCheck
  public ServicePointVersion updateServicePointVersion(@TerminationCheckParameter ServicePointVersion currentVersion,
      ServicePointVersion editedVersion,
      List<ServicePointVersion> currentVersions) {
    servicePointVersionRepository.incrementVersion(currentVersion.getNumber());
    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(ServicePointVersion.class.getSimpleName(), "version");
    }
    editedVersion.setNumber(currentVersion.getNumber());
    editedVersion.setSloid(currentVersion.getSloid());
    editedVersion.setNumberShort(currentVersion.getNumberShort());
    editedVersion.setCountry(currentVersion.getCountry());

    List<ServicePointVersion> existingDbVersions = findAllByNumberOrderByValidFrom(currentVersion.getNumber());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, existingDbVersions);
    List<ServicePointVersion> existingDbVersionInReview = existingDbVersions.stream()
        .filter(servicePointVersion -> Status.IN_REVIEW == servicePointVersion.getStatus()).toList();

    versionableService.applyVersioning(ServicePointVersion.class, versionedObjects,
        version -> save(version, Optional.of(currentVersion), currentVersions),
        new ApplyVersioningDeleteByIdLongConsumer(servicePointVersionRepository));

    List<ServicePointVersion> afterUpdateServicePoint = servicePointValidationService.validateNoMergeAffectVersionInReview(
        currentVersion, existingDbVersionInReview);

    servicePointTerminationService.checkTerminationAllowed(currentVersions, afterUpdateServicePoint);
    return editedVersion;
  }

  private ServicePointVersion save(ServicePointVersion servicePointVersion, Optional<ServicePointVersion> currentVersion,
      List<ServicePointVersion> currentVersions) {
    preSaveChecks(servicePointVersion, currentVersion, currentVersions);
    return servicePointVersionRepository.saveAndFlush(servicePointVersion);
  }

  private void preSaveChecks(@TerminationCheckParameter ServicePointVersion servicePointVersion,
      Optional<ServicePointVersion> currentVersion,
      List<ServicePointVersion> currentVersions) {

    Status status = ServicePointStatusDecider.getStatusForServicePoint(servicePointVersion, currentVersion, currentVersions);
    servicePointVersion.setStatus(status);
    servicePointValidationService.validateAndSetAbbreviation(servicePointVersion);
    servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion);
  }

  public ReadServicePointVersionModel updateDesignationOfficial(Long id,
      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel) {
    ServicePointVersion servicePointVersionToUpdate = findById(id).orElseThrow(
        () -> new NotFoundException.IdNotFoundException(id));

    List<ServicePointVersion> currentVersions = findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber());

    ServicePointVersion editedVersion = currentVersions.stream()
        .filter(version -> servicePointVersionToUpdate.getId().equals(version.getId()))
        .findFirst()
        .orElseThrow(() -> new NotFoundException.IdNotFoundException(servicePointVersionToUpdate.getId()))
        .toBuilder().designationOfficial(updateDesignationOfficialServicePointModel.getDesignationOfficial())
        .build();

    return ServicePointVersionMapper.toModel(
        updateServicePointVersion(servicePointVersionToUpdate, editedVersion, currentVersions));
  }

  public ReadServicePointVersionModel terminateServicePoint(Long id,
      TerminateServicePointModel terminateServicePointModel) {
    ServicePointVersion servicePointVersionToUpdate = findById(id).orElseThrow(
        () -> new NotFoundException.IdNotFoundException(id));
    DateRange dateRange = new DateRange(servicePointVersionToUpdate.getValidFrom(), servicePointVersionToUpdate.getValidTo());

    TerminationHelper.isValidToInLastVersionRange(
        servicePointVersionToUpdate.getSloid(), dateRange, terminateServicePointModel.getValidTo());

    List<ServicePointVersion> currentVersions = findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber());

    ServicePointVersion editedVersion = currentVersions.stream()
        .filter(version -> servicePointVersionToUpdate.getId().equals(version.getId()))
        .findFirst()
        .orElseThrow(() -> new NotFoundException.IdNotFoundException(servicePointVersionToUpdate.getId()))
        .toBuilder().validTo(terminateServicePointModel.getValidTo())
        .build();

    return ServicePointVersionMapper.toModel(
        updateServicePointVersion(servicePointVersionToUpdate, editedVersion, currentVersions));
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(#servicePointVersion,
      #servicePointVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @TerminationCheck
  public ServicePointVersion updateStopPointStatusForWorkflow(@TerminationCheckParameter ServicePointVersion servicePointVersion,
      List<ServicePointVersion> servicePointVersions, Status statusToChange) {
    ServicePointHelper.validateIsStopPointLocatedInSwitzerland(servicePointVersion);
    StatusTransitionDecider.validateWorkflowStatusTransition(servicePointVersion.getStatus(), statusToChange);
    servicePointVersion.setStatus(statusToChange);
    servicePointVersionRepository.save(servicePointVersion);
    return servicePointVersion;
  }

  @PreAuthorize("""
      @countryAndBusinessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdateCountryBased(#servicePointVersion,
      #servicePointVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  public ServicePointVersion updateStopPointTerminationStatus(ServicePointVersion servicePointVersion,
      List<ServicePointVersion> servicePointVersions, UpdateTerminationServicePointModel updateTerminationServicePointModel) {
    servicePointVersions.forEach(
        spv -> spv.setTerminationInProgress(updateTerminationServicePointModel.isTerminationInProgress()));
    servicePointVersionRepository.saveAll(servicePointVersions);
    return servicePointVersion;
  }

  public List<ServicePointSwissWithGeoTransfer> findActualServicePointWithGeolocation() {
    return servicePointVersionRepository.findActualServicePointWithGeolocation();
  }

  public void publishAllServicePoints() {
    log.info("Syncing all Service Points");
    servicePointDistributor.syncServicePoints();
  }

  public List<ServicePointVersion> findFareStopsToCleanup() {
    return servicePointVersionRepository.findFareStopsToCleanup();
  }
}
