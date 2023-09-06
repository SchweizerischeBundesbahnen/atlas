package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.controller.BusinessOrganisationSearchRestrictions;
import ch.sbb.business.organisation.directory.controller.BusinessOrganisationVersionSearchRestrictions;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BusinessOrganisationService {

  private final BusinessOrganisationVersionRepository versionRepository;
  private final BusinessOrganisationRepository repository;
  private final VersionableService versionableService;
  private final BusinessOrganisationValidationService validationService;
  private final BusinessOrganisationDistributor businessOrganisationDistributor;

  public Page<BusinessOrganisation> getBusinessOrganisations(
      BusinessOrganisationSearchRestrictions searchRestrictions) {
    return repository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public BusinessOrganisation findBusinessOrganisationBySboid(String sboid) {
    return repository.findBySboid(sboid);
  }

  public Page<BusinessOrganisationVersion> getBusinessOrganisationVersions(
      BusinessOrganisationVersionSearchRestrictions searchRestrictions) {
    return versionRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public BusinessOrganisationVersion save(BusinessOrganisationVersion version) {
    version.setStatus(Status.VALIDATED);
    validationService.validatePreconditionBusinessRule(version);
    BusinessOrganisationVersion savedVersion = versionRepository.saveAndFlush(version);
    businessOrganisationDistributor.saveToDistributedServices(savedVersion);
    return savedVersion;
  }

  public List<BusinessOrganisationVersion> findBusinessOrganisationVersions(String sboid) {
    return versionRepository.findAllBySboidOrderByValidFrom(sboid);
  }

  public BusinessOrganisationVersion findById(Long id) {
    return versionRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public void updateBusinessOrganisationVersion(
      BusinessOrganisationVersion currentVersion, BusinessOrganisationVersion editedVersion) {
    versionRepository.incrementVersion(currentVersion.getSboid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
        .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(BusinessOrganisationVersion.class.getSimpleName(),
          "version");
    }

    List<BusinessOrganisationVersion> currentVersions = versionRepository.findAllBySboidOrderByValidFrom(
        currentVersion.getSboid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);
    versionableService.applyVersioning(BusinessOrganisationVersion.class, versionedObjects,
        this::save, this::deleteById);
  }

  void deleteById(long id) {
    BusinessOrganisationVersion existingEntity = findById(id);
    versionRepository.deleteById(id);
    businessOrganisationDistributor.deleteOnDistributedServices(existingEntity);
  }

  public void deleteAll(List<BusinessOrganisationVersion> versions) {
    versionRepository.deleteAll(versions);
    versions.forEach(businessOrganisationDistributor::deleteOnDistributedServices);
  }

  public List<BusinessOrganisationVersion> revokeBusinessOrganisation(String sboid) {
    List<BusinessOrganisationVersion> versions = findBusinessOrganisationVersions(sboid);
    versions.forEach(version -> {
      version.setStatus(Status.REVOKED);
      businessOrganisationDistributor.saveToDistributedServices(version);
    });
    return versions;
  }

  public void syncAllBusinessOrganisations() {
    versionRepository.findAll().forEach(businessOrganisationDistributor::saveToDistributedServices);
  }
}
