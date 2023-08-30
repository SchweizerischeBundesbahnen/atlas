package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.model.search.SublineSearchRestrictions;
import ch.sbb.line.directory.repository.SublineRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.SublineValidationService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Transactional
public class SublineService {

  private final SublineVersionRepository sublineVersionRepository;
  private final SublineRepository sublineRepository;
  private final VersionableService versionableService;
  private final LineService lineService;
  private final SublineValidationService sublineValidationService;
  private final CoverageService coverageService;

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  public SublineVersion create(SublineVersion businessObject) {
    return save(businessObject);
  }

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, #currentVersions, T(ch.sbb.atlas.kafka"
      + ".model.user.admin.ApplicationType).LIDI)")
  public void update(SublineVersion currentVersion, SublineVersion editedVersion, List<SublineVersion> currentVersions) {
    updateVersion(currentVersion, editedVersion);
  }

  public Page<Subline> findAll(SublineSearchRestrictions searchRestrictions) {
    return sublineRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public List<SublineVersion> findSubline(String slnid) {
    return sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public Optional<SublineVersion> findById(Long id) {
    return sublineVersionRepository.findById(id);
  }

  public SublineVersion save(SublineVersion sublineVersion) {
    sublineVersion.setStatus(Status.VALIDATED);
    List<LineVersion> lineVersions = lineService.findLineVersions(
        sublineVersion.getMainlineSlnid());
    if (lineVersions.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Main line with SLNID " + sublineVersion.getMainlineSlnid() + " does not exist");
    }

    sublineValidationService.validatePreconditionSublineBusinessRules(sublineVersion);
    sublineVersionRepository.saveAndFlush(sublineVersion);
    sublineValidationService.validateSublineAfterVersioningBusinessRule(sublineVersion);
    return sublineVersion;
  }

  public List<SublineVersion> revokeSubline(String slnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    sublineVersions.forEach(sublineVersion -> sublineVersion.setStatus(Status.REVOKED));
    return sublineVersions;
  }

  public void deleteById(Long id) {
    SublineVersion sublineVersion = sublineVersionRepository.findById(id)
        .orElseThrow(
            () -> new IdNotFoundException(id));
    coverageService.deleteCoverageSubline(sublineVersion.getSlnid());
    sublineVersionRepository.deleteById(id);
  }

  public void deleteAll(String slnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        slnid);
    if (sublineVersions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    sublineVersionRepository.deleteAll(sublineVersions);
  }

  public void updateVersion(SublineVersion currentVersion, SublineVersion editedVersion) {
    sublineVersionRepository.incrementVersion(currentVersion.getSlnid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
        .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(SublineVersion.class.getSimpleName(), "version");
    }
    List<SublineVersion> currentVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(SublineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

}
