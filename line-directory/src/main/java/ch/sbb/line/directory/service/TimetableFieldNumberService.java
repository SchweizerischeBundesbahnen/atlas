package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.TimetableFieldNumberConflictException;
import ch.sbb.line.directory.model.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TimetableFieldNumberService {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final VersionableService versionableService;

  public List<TimetableFieldNumberVersion> getAllVersionsVersioned(String ttfnId) {
    return versionRepository.getAllVersionsVersioned(ttfnId);
  }

  public Optional<TimetableFieldNumberVersion> findById(Long id) {
    return versionRepository.findById(id);
  }

  public TimetableFieldNumberVersion save(TimetableFieldNumberVersion newVersion) {
    newVersion.setStatus(Status.ACTIVE);
    List<TimetableFieldNumberVersion> overlappingVersions = getOverlapsOnNumberAndSttfn(newVersion);
    if (!overlappingVersions.isEmpty()) {
      throw new TimetableFieldNumberConflictException(newVersion, overlappingVersions);
    }
    return versionRepository.saveAndFlush(newVersion);
  }

  public Page<TimetableFieldNumber> getVersionsSearched(TimetableFieldNumberSearchRestrictions searchRestrictions) {
    return timetableFieldNumberRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public void updateVersion(TimetableFieldNumberVersion currentVersion, TimetableFieldNumberVersion editedVersion) {
    versionRepository.incrementVersion(currentVersion.getTtfnid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(TimetableFieldNumberVersion.class.getSimpleName(), "version");
    }

    List<TimetableFieldNumberVersion> currentVersions = getAllVersionsVersioned(currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(TimetableFieldNumberVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

  public List<TimetableFieldNumberVersion> getOverlapsOnNumberAndSttfn(
      TimetableFieldNumberVersion version) {
    String ttfnid = version.getTtfnid() == null ? "" : version.getTtfnid();
    return versionRepository.getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(version.getNumber(), version.getSwissTimetableFieldNumber().toLowerCase(),
        version.getValidFrom(), version.getValidTo(), ttfnid);
  }

  public void deleteAll(List<TimetableFieldNumberVersion> allVersionsVersioned) {
    versionRepository.deleteAll(allVersionsVersioned);
  }
}
