package ch.sbb.line.directory.service;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.base.service.versioning.model.VersionedObject;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
  private final LineStatusDecider lineStatusDecider;

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

  @PreAuthorize("@userAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersion create(LineVersion businessObject) {
    return save(businessObject, Collections.emptyList());
  }

  @PreAuthorize("@userAdministrationService.hasUserPermissionsToUpdate(#editedVersion, #currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public void update(LineVersion currentVersion, LineVersion editedVersion, List<LineVersion> currentVersions) {
    updateVersion(currentVersion, editedVersion);
  }

  public List<LineVersion> revokeLine(String slnid) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    lineVersions.forEach(lineVersion -> lineVersion.setStatus(Status.REVOKED));
    return lineVersions;
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

  public List<Line> getAllCoveredLines() {
    return lineRepository.getAllCoveredLines();
  }

  public List<LineVersion> getAllCoveredLineVersions() {
    return lineVersionRepository.getAllCoveredLineVersions();
  }

  LineVersion save(LineVersion lineVersion, List<LineVersion> currentLineVersions) {
    lineVersion.setStatus(lineStatusDecider.getStatusForLine(lineVersion, currentLineVersions));
    lineValidationService.validateLinePreconditionBusinessRule(lineVersion);
    lineVersionRepository.saveAndFlush(lineVersion);
    lineValidationService.validateLineAfterVersioningBusinessRule(lineVersion);
    return lineVersion;
  }

  void updateVersion(LineVersion currentVersion, LineVersion editedVersion) {
    lineVersionRepository.incrementVersion(currentVersion.getSlnid());
    lineValidationService.validateLineForUpdate(currentVersion, editedVersion);
    updateVersion(currentVersion, editedVersion, findLineVersions(currentVersion.getSlnid()));
  }

  private void updateVersion(LineVersion currentVersion, LineVersion editedVersion,
      List<LineVersion> currentVersions) {
    if (editedVersion.getVersion() != null && !currentVersion.getVersion()
                                                             .equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(LineVersion.class.getSimpleName(), "version");
    }

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    List<LineVersion> preSaveVersions = currentVersions.stream().map(this::copyLineVersion).toList();
    versionableService.applyVersioning(LineVersion.class, versionedObjects, version -> save(version, preSaveVersions),
        this::deleteById);
  }

  private LineVersion copyLineVersion(LineVersion lineVersion) {
    return LineVersion.builder()
        .id(lineVersion.getId())
        .status(lineVersion.getStatus())
        .lineType(lineVersion.getLineType())
        .slnid(lineVersion.getSlnid())
        .paymentType(lineVersion.getPaymentType())
        .number(lineVersion.getNumber())
        .alternativeName(lineVersion.getAlternativeName())
        .combinationName(lineVersion.getCombinationName())
        .longName(lineVersion.getLongName())
        .colorFontRgb(lineVersion.getColorFontRgb())
        .colorBackRgb(lineVersion.getColorBackRgb())
        .colorFontCmyk(lineVersion.getColorFontCmyk())
        .colorBackCmyk(lineVersion.getColorBackCmyk())
        .description(lineVersion.getDescription())
        .icon(lineVersion.getIcon())
        .validFrom(lineVersion.getValidFrom())
        .validTo(lineVersion.getValidTo())
        .businessOrganisation(lineVersion.getBusinessOrganisation())
        .comment(lineVersion.getComment())
        .swissLineNumber(lineVersion.getSwissLineNumber())
        .version(lineVersion.getVersion())
        .creator(lineVersion.getCreator())
        .creationDate(lineVersion.getCreationDate())
        .editor(lineVersion.getEditor())
        .editionDate(lineVersion.getEditionDate())
        .build();
  }

}
