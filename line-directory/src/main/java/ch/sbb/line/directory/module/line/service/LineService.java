package ch.sbb.line.directory.module.line.service;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.revoke.service.RevokeService;
import ch.sbb.atlas.versioning.convert.ReflectionHelper;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.model.SublineVersionRange;
import ch.sbb.line.directory.module.line.entity.Line;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.exception.LineDeleteConflictException;
import ch.sbb.line.directory.module.line.repository.LineRepository;
import ch.sbb.line.directory.module.line.repository.LineVersionRepository;
import ch.sbb.line.directory.module.line.search.LineSearchRestrictions;
import ch.sbb.line.directory.module.line.search.LineVersionSearchRestrictions;
import ch.sbb.line.directory.module.line.validation.LineUpdateValidationService;
import ch.sbb.line.directory.module.line.validation.LineValidationService;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import ch.sbb.line.directory.module.subline.repository.SublineVersionRepository;
import ch.sbb.line.directory.module.subline.service.SublineService;
import ch.sbb.line.directory.module.subline.service.SublineShorteningService;
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
public class LineService extends RevokeService<LineVersion> {

  private final LineVersionRepository lineVersionRepository;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineRepository lineRepository;
  private final VersionableService versionableService;
  private final LineValidationService lineValidationService;
  private final LineUpdateValidationService lineUpdateValidationService;
  private final SublineShorteningService sublineShorteningService;
  private final SublineService sublineService;

  public Page<Line> findAll(LineSearchRestrictions searchRestrictions) {
    return lineRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Page<LineVersion> findAllVersions(LineVersionSearchRestrictions searchRestrictions) {
    return lineVersionRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Optional<Line> findLine(String slnid) {
    return lineRepository.findAllBySlnid(slnid);
  }

  public List<LineVersion> findLineVersions(String slnid) {
    return findBySid4ptOrderByValidFrom(slnid);
  }

  public List<LineVersion> findLineVersionsForV1(String slnid) {
    return findBySid4ptOrderByValidFrom(slnid).stream()
        .filter(i -> i.getSwissLineNumber() != null)
        .toList();
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  public LineVersion getLineVersionById(Long id) {
    return findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas"
      + ".kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersion create(LineVersion businessObject) {
    return save(businessObject);
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#lineVersion, T(ch.sbb.atlas"
      + ".kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersion createV2(LineVersion lineVersion) {
    lineValidationService.dynamicBeanValidation(lineVersion);
    LineVersion savedLineVersion = save(lineVersion);
    lineValidationService.validateLineAfterVersioningBusinessRule(savedLineVersion);
    return savedLineVersion;
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, "
      + "#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public void update(LineVersion currentVersion, LineVersion editedVersion, List<LineVersion> currentVersions) {
    lineValidationService.validateNotRevoked(currentVersion);
    lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentVersion, editedVersion);

    boolean isOnlyValidityChanged = sublineShorteningService.isOnlyValidityChanged(currentVersion, editedVersion);
    boolean isShortening = sublineShorteningService.isShortening(currentVersion, editedVersion);

    if (isOnlyValidityChanged && isShortening) {
      shortSublines(currentVersion, editedVersion);
    }

    updateVersion(currentVersion, editedVersion);
  }

  public List<LineVersion> revokeLine(String slnid) {
    return revoke(slnid);
  }

  @Override
  protected void saveAll(List<LineVersion> lineVersions) {
    lineVersionRepository.saveAll(lineVersions);
  }

  @Override
  protected List<LineVersion> findBySid4ptOrderByValidFrom(String slnid) {
    return lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public void skipWorkflow(Long lineVersionId) {
    LineVersion lineVersion = getLineVersionById(lineVersionId);
    if (lineVersion.getStatus() == Status.DRAFT) {
      lineVersion.setStatus(Status.VALIDATED);
      lineVersionRepository.save(lineVersion);
    }
  }

  void deleteById(Long id) {
    if (!lineVersionRepository.existsById(id)) {
      throw new IdNotFoundException(id);
    }
    lineVersionRepository.deleteById(id);
    sublineVersionRepository.flush();
  }

  public void deleteAll(String slnid) {
    List<LineVersion> currentVersions = findBySid4ptOrderByValidFrom(slnid);

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

  LineVersion save(LineVersion lineVersion) {
    return save(lineVersion, Optional.empty(), Collections.emptyList());
  }

  LineVersion save(LineVersion lineVersion, Optional<LineVersion> currentLineVersion, List<LineVersion> currentLineVersions) {
    lineVersion.setStatus(Status.VALIDATED);
    lineValidationService.validateLinePreconditionBusinessRule(lineVersion);
    lineVersionRepository.saveAndFlush(lineVersion);
    return lineVersion;
  }

  public void updateVersion(LineVersion currentVersion, LineVersion editedVersion) {
    lineVersionRepository.incrementVersion(currentVersion.getSlnid());
    editedVersion.setSlnid(currentVersion.getSlnid());

    List<LineVersion> currentVersions = findLineVersions(currentVersion.getSlnid());

    if (editedVersion.getLineType() != LineType.ORDERLY) {
      editedVersion.setSwissLineNumber(currentVersion.getSwissLineNumber());
      editedVersion.setConcessionType(currentVersion.getConcessionType());
    }
    updateVersion(currentVersion, editedVersion, currentVersions);
  }

  private void updateVersion(LineVersion currentVersion, LineVersion editedVersion,
      List<LineVersion> currentVersions) {
    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(LineVersion.class.getSimpleName(), "version");
    }

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);
    versionableService.doNotAllowGaps(versionedObjects);

    List<LineVersion> preSaveVersions = currentVersions.stream().map(ReflectionHelper::copyObjectViaBuilder).toList();
    versionableService.applyVersioning(LineVersion.class, versionedObjects,
        version -> save(version, Optional.of(currentVersion), preSaveVersions),
        this::deleteById);
    lineValidationService.validateLineAfterVersioningBusinessRule(editedVersion);
  }

  private void shortSublines(LineVersion currentVersion, LineVersion editedVersion) {
    List<SublineVersionRange> sublinesToShort = sublineShorteningService.checkAndPrepareToShortSublines(currentVersion,
        editedVersion);

    if (!sublinesToShort.isEmpty()) {
      for (SublineVersionRange sublineToShort : sublinesToShort) {
        sublineService.updateVersion(sublineToShort.getOldestVersion(), sublineToShort.getLatestVersion());
      }
    }
  }

}
