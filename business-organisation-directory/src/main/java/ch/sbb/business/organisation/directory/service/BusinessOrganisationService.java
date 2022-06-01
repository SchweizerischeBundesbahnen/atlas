package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.controller.BusinessOrganisationSearchRestrictions;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  public Page<BusinessOrganisation> getBusinessOrganisations(
      BusinessOrganisationSearchRestrictions searchRestrictions) {
    return repository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public BusinessOrganisationVersion save(BusinessOrganisationVersion version) {
    version.setStatus(Status.ACTIVE);
    validationService.validateLinePreconditionBusinessRule(version);
    return versionRepository.save(version);
  }

  public List<BusinessOrganisationVersion> findBusinessOrganisationVersions(String sboid) {
    return versionRepository.findAllBySboidOrderByValidFrom(sboid);
  }

  public BusinessOrganisationVersion findById(Long id) {
    return versionRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public void updateBusinessOrganisationVersion(
      BusinessOrganisationVersion currentVersion, BusinessOrganisationVersion editedVersion) {
    List<BusinessOrganisationVersion> currentVersions = versionRepository.findAllBySboidOrderByValidFrom(
        currentVersion.getSboid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);
    versionableService.applyVersioning(BusinessOrganisationVersion.class, versionedObjects,
        this::save, this::deleteById);
  }

  void deleteById(long id) {
    findById(id);
    versionRepository.deleteById(id);
  }

  public void deleteAll(List<BusinessOrganisationVersion> versions) {
    versionRepository.deleteAll(versions);
  }

}
