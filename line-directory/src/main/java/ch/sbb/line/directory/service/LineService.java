package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.LineDeleteConflictException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.model.LineSearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.LineValidationService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LineService {

  private final LineVersionRepository lineVersionRepository;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineRepository lineRepository;
  private final VersionableService versionableService;
  private final LineValidationService lineValidationService;
  private final CoverageService coverageService;

  public Page<Line> findAll(LineSearchRestrictions searchRestrictions) {
    return lineRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Optional<Line> findLine(String slnid) {
    return lineRepository.findAllBySlnid(slnid);
  }

  public List<LineVersion> findLineVersions(String slnid) {
    return lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  public LineVersion save(LineVersion lineVersion) {
    lineVersion.setStatus(Status.ACTIVE);
    lineValidationService.validateLinePreconditionBusinessRule(lineVersion);
    lineVersionRepository.saveAndFlush(lineVersion);
    lineValidationService.validateLineAfterVersioningBusinessRule(lineVersion);
    return lineVersion;
  }

  public void deleteById(Long id) {
    LineVersion lineVersion = lineVersionRepository.findById(id).orElseThrow(
        () -> new IdNotFoundException(id));
    coverageService.deleteCoverageLine(lineVersion.getSlnid());
    lineVersionRepository.deleteById(id);
  }

  public void deleteAll(String slnid) {
    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);

    if (currentVersions.isEmpty()) {
      throw new SlnidNotFoundException(slnid);
    }
    List<SublineVersion> sublineVersionRelatedToLine = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        slnid);
    if (!sublineVersionRelatedToLine.isEmpty()) {
      throw new LineDeleteConflictException(slnid, sublineVersionRelatedToLine);
    }

    lineVersionRepository.deleteAll(currentVersions);
  }

  public void updateVersion(LineVersion currentVersion,
      LineVersion editedVersion) {
    lineVersionRepository.incrementVersion(currentVersion.getSlnid());
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
                                                             .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(LineVersion.class.getSimpleName(), "version");
    }

    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(LineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

  public List<Line> getAllCoveredLines() {
    return lineRepository.getAllCoveredLines();
  }

  public List<LineVersion> getAllCoveredLineVersions() {
    return lineVersionRepository.getAllCoveredLineVersions();
  }
}
