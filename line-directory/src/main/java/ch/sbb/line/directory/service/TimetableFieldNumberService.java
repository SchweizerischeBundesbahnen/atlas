package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.exception.TimetableFieldNumberConflictException;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class TimetableFieldNumberService {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final VersionableService versionableService;

  @Autowired
  public TimetableFieldNumberService(TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberRepository timetableFieldNumberRepository,
      VersionableService versionableService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
    this.versionableService = versionableService;
  }

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
    return versionRepository.save(newVersion);
  }

  public Page<TimetableFieldNumber> getVersionsSearched(Pageable pageable,
      List<String> searchCriteria,
      LocalDate validOn,
      List<Status> statusChoices) {
    return timetableFieldNumberRepository.searchVersions(pageable, searchCriteria, validOn, statusChoices);
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public List<VersionedObject> updateVersion(TimetableFieldNumberVersion currentVersion, TimetableFieldNumberVersion editedVersion) {
    List<TimetableFieldNumberVersion> currentVersions = getAllVersionsVersioned(currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(TimetableFieldNumberVersion.class, versionedObjects, this::save,
        this::deleteById);
    return versionedObjects;
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
