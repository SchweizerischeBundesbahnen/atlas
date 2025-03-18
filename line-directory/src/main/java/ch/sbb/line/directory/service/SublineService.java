package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.DateRangeConflictException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.model.LineVersionRange;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.SublineValidationService;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SublineService {

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;
  private final VersionableService versionableService;
  private final SublineValidationService sublineValidationService;

  @Transactional
  @PreAuthorize("""
      @businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#businessObject, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  public SublineVersion create(SublineVersion businessObject) {
    return save(businessObject);
  }

  @Transactional
  @PreAuthorize("""
      @businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, #currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  public void update(SublineVersion currentVersion, SublineVersion editedVersion, List<SublineVersion> currentVersions) {
    updateVersion(currentVersion, editedVersion);
  }

  public List<SublineVersion> findSubline(String slnid) {
    return sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public Optional<SublineVersion> findById(Long id) {
    return sublineVersionRepository.findById(id);
  }

  SublineVersion save(SublineVersion sublineVersion) {
    sublineVersion.setStatus(Status.VALIDATED);
    sublineValidationService.validatePreconditionSublineBusinessRules(sublineVersion);
    validateSublineValidity(sublineVersion);
    SublineVersion savedVersion = sublineVersionRepository.saveAndFlush(sublineVersion);
    return savedVersion;
  }

  public void revokeSubline(String slnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    sublineVersions.forEach(sublineVersion -> sublineVersion.setStatus(Status.REVOKED));
    sublineVersionRepository.saveAll(sublineVersions);
  }

  @Transactional
  public void deleteById(Long id) {
    if (!sublineVersionRepository.existsById(id)) {
      throw new IdNotFoundException(id);
    }
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

  void updateVersion(SublineVersion currentVersion, SublineVersion editedVersion) {
    sublineVersionRepository.incrementVersion(currentVersion.getSlnid());
    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(SublineVersion.class.getSimpleName(), "version");
    }

    editedVersion.setSlnid(currentVersion.getSlnid());
    editedVersion.setSublineType(currentVersion.getSublineType());
    editedVersion.setMainlineSlnid(currentVersion.getMainlineSlnid());

    List<SublineVersion> currentVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(SublineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

  public LineVersion getMainLineVersion(String mainSlnid) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(mainSlnid);

    return OverviewDisplayBuilder.getPrioritizedVersion(lineVersions);
  }

  public void validateSublineValidity(SublineVersion sublineVersion) {
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(sublineVersion.getMainlineSlnid());
    LineVersionRange lineVersionRange = getOldestAndLatestLine(lineVersions);

    DateRange dateRangeMainline =
        new DateRange(lineVersionRange.getOldestVersion().getValidFrom(), lineVersionRange.getLatestVersion().getValidTo());
    DateRange dateRangeSubline = new DateRange(sublineVersion.getValidFrom(), sublineVersion.getValidTo());

    if (!dateRangeSubline.isDateRangeContainedIn(dateRangeMainline)) {
      throw new DateRangeConflictException(dateRangeMainline);
    }
  }

  private LineVersionRange getOldestAndLatestLine(List<LineVersion> lines) {
    LineVersion oldest = lines.stream()
        .min(Comparator.comparing(LineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    LineVersion latest = lines.stream()
        .max(Comparator.comparing(LineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    return new LineVersionRange(oldest, latest);
  }
}
