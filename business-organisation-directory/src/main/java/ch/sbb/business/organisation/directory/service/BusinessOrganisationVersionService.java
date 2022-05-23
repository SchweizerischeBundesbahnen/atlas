package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BusinessOrganisationVersionService {

  private final BusinessOrganisationRepository repository;
  private final VersionableService versionableService;

  public List<BusinessOrganisationVersion> getBusinessOrganisations() {
    return repository.findAll();
  }

  public BusinessOrganisationVersion save(BusinessOrganisationVersion version) {
    version.setStatus(Status.ACTIVE);
    return repository.save(version);
  }

  public List<BusinessOrganisationVersion> findBusinessOrganisationVersions(String sboid) {
    return repository.findAllBySboidOrderByValidFrom(sboid);
  }

  public BusinessOrganisationVersion findById(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public void updateBusinessOrganisationVersion(
      BusinessOrganisationVersion currentVersion, BusinessOrganisationVersion editedVersion) {
    List<BusinessOrganisationVersion> currentVersions = repository.findAllBySboidOrderByValidFrom(
        currentVersion.getSboid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);
    versionableService.applyVersioning(BusinessOrganisationVersion.class, versionedObjects,
        this::save, this::deleteById);
  }

  private void deleteById(long id) {
    findById(id);
    repository.deleteById(id);
  }

}
