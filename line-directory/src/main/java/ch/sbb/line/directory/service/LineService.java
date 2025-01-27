package ch.sbb.line.directory.service;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.AffectedSublines;
import ch.sbb.atlas.api.lidi.ShortenSubline;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.convert.ReflectionHelper;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.LineDeleteConflictException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.model.SublineVersionRange;
import ch.sbb.line.directory.model.search.LineSearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.LineUpdateValidationService;
import ch.sbb.line.directory.validation.LineValidationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LineService {

  public enum AdjustmentDateStatus {
    VALID_FROM_CHANGED,
    VALID_TO_CHANGED,
    BOTH_CHANGED,
    NO_CHANGE
  }

  private final LineVersionRepository lineVersionRepository;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineRepository lineRepository;
  private final VersionableService versionableService;
  private final LineValidationService lineValidationService;
  private final LineUpdateValidationService lineUpdateValidationService;
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

  public List<LineVersion> findLineVersionsForV1(String slnid) {
    return lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid).stream()
        .filter(i -> i.getSwissLineNumber() != null)
        .toList();
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas"
      + ".kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersion create(LineVersion businessObject) {
    return save(businessObject);
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas"
      + ".kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersion createV2(LineVersion businessObject) {
    lineValidationService.dynamicBeanValidation(businessObject);
    return save(businessObject);
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, "
      + "#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public void update(LineVersion currentVersion, LineVersion editedVersion, List<LineVersion> currentVersions) {
    lineValidationService.validateNotRevoked(currentVersion);
    lineUpdateValidationService.validateFieldsNotUpdatableForLineTypeOrderly(currentVersion, editedVersion);
    updateVersion(currentVersion, editedVersion);
  }

  public List<LineVersion> revokeLine(String slnid) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    lineVersions.forEach(lineVersion -> lineVersion.setStatus(Status.REVOKED));
    lineVersionRepository.saveAll(lineVersions);
    return lineVersions;
  }

  public void skipWorkflow(Long lineVersionId) {
    LineVersion lineVersion = findById(lineVersionId).orElseThrow(() -> new IdNotFoundException(lineVersionId));
    if (lineVersion.getStatus() == Status.DRAFT) {
      lineVersion.setStatus(Status.VALIDATED);
      lineVersionRepository.save(lineVersion);
    }
  }

  @Transactional
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

  LineVersion save(LineVersion lineVersion) {
    return save(lineVersion, Optional.empty(), Collections.emptyList());
  }

  LineVersion save(LineVersion lineVersion, Optional<LineVersion> currentLineVersion, List<LineVersion> currentLineVersions) {
    lineVersion.setStatus(lineStatusDecider.getStatusForLine(lineVersion, currentLineVersion, currentLineVersions));
    lineValidationService.validateLinePreconditionBusinessRule(lineVersion);
    lineVersionRepository.saveAndFlush(lineVersion);
    lineValidationService.validateLineAfterVersioningBusinessRule(lineVersion);
    return lineVersion;
  }

  public void updateVersion(LineVersion currentVersion, LineVersion editedVersion) {
    lineVersionRepository.incrementVersion(currentVersion.getSlnid());
    editedVersion.setSlnid(currentVersion.getSlnid());

    //checkAffectedSublines(editedVersion);

    List<LineVersion> currentVersions = findLineVersions(currentVersion.getSlnid());
    lineUpdateValidationService.validateLineForUpdate(currentVersion, editedVersion, currentVersions);

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
    lineUpdateValidationService.validateVersioningNotAffectingReview(currentVersions, versionedObjects);

    List<LineVersion> preSaveVersions = currentVersions.stream().map(ReflectionHelper::copyObjectViaBuilder).toList();
    versionableService.applyVersioning(LineVersion.class, versionedObjects,
        version -> save(version, Optional.of(currentVersion), preSaveVersions),
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

  public AffectedSublines checkAffectedSublines(Long id, UpdateLineVersionModelV2 updateLineVersionModelV2) {
    List<String> notAllowedSublines = new ArrayList<>();
    List<String> allowedSublines = new ArrayList<>();

    LineVersion lineVersion = findById(id).orElseThrow(() -> new IdNotFoundException(id));

    Map<String, List<SublineVersion>> sublineVersions = getAllSublinesByMainlineSlnid(lineVersion.getSlnid());

    for (List<SublineVersion> list : sublineVersions.values()) {
      SublineVersionRange sublineVersionRange = getOldestAndLatest(list);
      if (isShorteningAllowed(lineVersion, sublineVersionRange.getOldestVersion(), sublineVersionRange.getLatestVersion())
          || list.size() == 1) {
        allowedSublines.add(sublineVersionRange.getLatestVersion().getSlnid());
      } else {
        notAllowedSublines.add(sublineVersionRange.getLatestVersion().getSlnid());
      }
    }

    return new AffectedSublines(allowedSublines, notAllowedSublines);
  }

  public void shortSublines(Long id, ShortenSubline shortenSubline) {
    LineVersion lineVersion = findById(id).orElseThrow(() -> new IdNotFoundException(id));

    for (String slndid : shortenSubline.getAllowedSublines()) {
      boolean changed = false;

      List<SublineVersion> list = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slndid);
      SublineVersionRange sublineVersionRange = getOldestAndLatest(list);

      if (isSublineValidityAffectedByUpdatedMainline(shortenSubline.getUpdateLineVersionModelV2(), sublineVersionRange)) {
        if (!shortenSubline.getUpdateLineVersionModelV2().getValidFrom().equals(lineVersion.getValidFrom())) {
          sublineVersionRange.getOldestVersion().setValidFrom(shortenSubline.getUpdateLineVersionModelV2().getValidFrom());
          changed = true;
        }
        if (!shortenSubline.getUpdateLineVersionModelV2().getValidTo().equals(lineVersion.getValidTo())) {
          sublineVersionRange.getLatestVersion().setValidTo(shortenSubline.getUpdateLineVersionModelV2().getValidTo());
          changed = true;
        }
        if (changed) {
          sublineVersionRepository.saveAll(
              List.of(sublineVersionRange.getOldestVersion(), sublineVersionRange.getLatestVersion()));
        }
      }
    }
  }

  private Map<String, List<SublineVersion>> getAllSublinesByMainlineSlnid(String mainlineSlnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(mainlineSlnid);
    return sublineVersions.stream()
        .collect(Collectors.groupingBy(SublineVersion::getSlnid));
  }

  private boolean hasMainlineSublines(List<SublineVersion> sublineVersions) {
    return !sublineVersions.isEmpty();
  }

  private boolean isSublineValidityAffectedByUpdatedMainline(UpdateLineVersionModelV2 updatedLineVersion,
      SublineVersionRange sublineVersionRange) {
    DateRange dateRangeSubline =
        new DateRange(sublineVersionRange.getOldestVersion().getValidFrom(), sublineVersionRange.getLatestVersion().getValidTo());
    DateRange dateRangeMainline = new DateRange(updatedLineVersion.getValidFrom(), updatedLineVersion.getValidTo());

    return !dateRangeSubline.isDateRangeContainedIn(dateRangeMainline);
  }

  //TODO check wenn subline vollständig ausserhalb der mainline liegt
  public boolean isShorteningAllowed(LineVersion lineVersion, SublineVersion oldestSublineVersion,
      SublineVersion latestSublineVersion) {
    return (lineVersion.getValidFrom().isBefore(oldestSublineVersion.getValidTo()) ||
        lineVersion.getValidFrom().isEqual(oldestSublineVersion.getValidTo()))
        && (lineVersion.getValidTo().isAfter(latestSublineVersion.getValidFrom()) ||
        lineVersion.getValidTo().isEqual(latestSublineVersion.getValidFrom()));

  }

  private SublineVersionRange getOldestAndLatest(List<SublineVersion> sublines) {
    SublineVersion oldest = sublines.stream()
        .min(Comparator.comparing(SublineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    SublineVersion latest = sublines.stream()
        .max(Comparator.comparing(SublineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    return new SublineVersionRange(oldest, latest);
  }
}
