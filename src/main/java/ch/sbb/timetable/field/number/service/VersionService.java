package ch.sbb.timetable.field.number.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.exceptions.ConflictException;
import ch.sbb.timetable.field.number.repository.TimetableFieldNumberRepository;
import ch.sbb.timetable.field.number.repository.VersionRepository;
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
public class VersionService {

  private final VersionRepository versionRepository;
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final VersionableService versionableService;

  @Autowired
  public VersionService(VersionRepository versionRepository,
      TimetableFieldNumberRepository timetableFieldNumberRepository,
      VersionableService versionableService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberRepository = timetableFieldNumberRepository;
    this.versionableService = versionableService;
  }

  public List<Version> getAllVersionsVersioned(String ttfnId) {
    return versionRepository.getAllVersionsVersioned(ttfnId);
  }

  public Optional<Version> findById(Long id) {
    return versionRepository.findById(id);
  }

  public Version save(Version newVersion) {
    newVersion.setStatus(Status.ACTIVE);
    if (!areNumberAndSttfnUnique(newVersion)) {
      throw new ConflictException("Number or SwissTimeTableFieldNumber are already taken");
    }
    return versionRepository.save(newVersion);
  }

  public boolean existsById(Long id) {
    return versionRepository.existsById(id);
  }

  public void deleteById(Long id) {
    versionRepository.deleteById(id);
  }

  public Page<TimetableFieldNumber> getOverview(Pageable pageable) {
    return timetableFieldNumberRepository.findAll(pageable);
  }

  public long count() {
    return versionRepository.count();
  }

  public List<VersionedObject> updateVersion(Version currentVersion, Version editedVersion) {
    List<Version> currentVersions = getAllVersionsVersioned(currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(Version.class, versionedObjects, this::save,
        this::deleteById);
    return versionedObjects;
  }

  private boolean areNumberAndSttfnUnique(Version version) {
    String ttfnid = version.getTtfnid() == null ? "" : version.getTtfnid();
    return versionRepository.getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(version.getNumber(), version.getSwissTimetableFieldNumber(),
        version.getValidFrom(), version.getValidTo(), ttfnid).size() == 0;
  }

  public void deleteAll(List<Version> allVersionsVersioned) {
    versionRepository.deleteAll(allVersionsVersioned);
  }
}
