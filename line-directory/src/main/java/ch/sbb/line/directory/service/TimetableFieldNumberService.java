package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TimetableFieldNumberService {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final TimetableFieldNumberValidationService timetableFieldNumberValidationService;
  private final VersionableService versionableService;

  public List<TimetableFieldNumberVersion> getAllVersionsVersioned(String ttfnId) {
    return versionRepository.getAllVersionsVersioned(ttfnId);
  }

  public List<TimetableFieldNumberVersion> revokeTimetableFieldNumber(String ttfnId) {
    List<TimetableFieldNumberVersion> timetableFieldNumberVersions =
        versionRepository.getAllVersionsVersioned(
            ttfnId);
    timetableFieldNumberVersions.forEach(
        timetableFieldNumberVersion -> timetableFieldNumberVersion.setStatus(Status.REVOKED));
    return timetableFieldNumberVersions;
  }

  public Optional<TimetableFieldNumberVersion> findById(Long id) {
    return versionRepository.findById(id);
  }

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TTFN)")
  public TimetableFieldNumberVersion create(TimetableFieldNumberVersion businessObject) {
    return save(businessObject);
  }

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, #currentVersions, T(ch.sbb.atlas.kafka"
      + ".model.user.admin.ApplicationType).TTFN)")
  public void update(TimetableFieldNumberVersion currentVersion, TimetableFieldNumberVersion editedVersion,
      List<TimetableFieldNumberVersion> currentVersions) {
    editedVersion.setTtfnid(currentVersion.getTtfnid());
    updateVersion(currentVersion, editedVersion);
  }

  public TimetableFieldNumberVersion save(TimetableFieldNumberVersion newVersion) {
    newVersion.setStatus(Status.VALIDATED);
    timetableFieldNumberValidationService.validatePreSave(newVersion);
    return versionRepository.saveAndFlush(newVersion);
  }

  public Page<TimetableFieldNumber> getVersionsSearched(
      TimetableFieldNumberSearchRestrictions searchRestrictions) {
    log.info("Loading TimetableFieldNumbers with searchRestrictions={}", searchRestrictions);
    return timetableFieldNumberRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public List<TimetableFieldNumberVersion> getVersionsValidAt(Set<String> ttfids, LocalDate validAt) {
    return versionRepository.getVersionsValidAt(ttfids, validAt);
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public void updateVersion(TimetableFieldNumberVersion currentVersion,
      TimetableFieldNumberVersion editedVersion) {
    versionRepository.incrementVersion(currentVersion.getTtfnid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
        .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(TimetableFieldNumberVersion.class.getSimpleName(),
          "version");
    }

    List<TimetableFieldNumberVersion> currentVersions = getAllVersionsVersioned(
        currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(TimetableFieldNumberVersion.class, versionedObjects,
        this::save,
        this::deleteById);
  }

  public void deleteAll(List<TimetableFieldNumberVersion> allVersionsVersioned) {
    versionRepository.deleteAll(allVersionsVersioned);
  }
}
