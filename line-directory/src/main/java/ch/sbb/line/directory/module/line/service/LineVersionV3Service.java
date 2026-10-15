package ch.sbb.line.directory.module.line.service;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.mapper.LineVersionV3Mapper;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import ch.sbb.line.directory.module.line.search.LineVersionV3SearchRestrictions;
import ch.sbb.line.directory.module.line.validation.LineVersionV3ValidationService;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineVersionV3Service {

  private final LineVersionV3Repository lineVersionV3Repository;
  private final VersionableService versionableService;
  private final LineVersionV3ValidationService lineVersionV3ValidationService;
  private final LineV3ShorteningService lineV3ShorteningService;

  public Page<LineVersionModelV3> findAll(LineVersionV3SearchRestrictions searchRestrictions) {
    Page<LineVersionV3> versions = lineVersionV3Repository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
    return new PageImpl<>(toModels(versions.getContent()), searchRestrictions.getPageable(), versions.getTotalElements());
  }

  public List<LineVersionModelV3> findBySlnid(String slnid) {
    return toModels(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid));
  }

  public LineVersionV3 getLineVersionById(Long id) {
    return lineVersionV3Repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public List<LineVersionV3> findLineVersions(String slnid) {
    return lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid);
  }

  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#newVersion, T(ch.sbb.atlas"
      + ".kafka.model.user.admin.ApplicationType).LIDI)")
  public LineVersionV3 create(LineVersionV3 newVersion) {
    LineVersionV3 savedVersion = save(newVersion);
    lineVersionV3ValidationService.validateLineAfterVersioningBusinessRule(findLineVersions(savedVersion.getSlnid()));
    return savedVersion;
  }

  /**
   * @return all versions of the slnid after versioning, ordered by validFrom
   */
  @Transactional
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.hasUserPermissionsToUpdate(#editedVersion, "
      + "#currentVersions, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)")
  public List<LineVersionV3> update(LineVersionV3 currentVersion, LineVersionV3 editedVersion,
      List<LineVersionV3> currentVersions) {
    lineVersionV3ValidationService.validateNotRevoked(currentVersion);
    lineVersionV3ValidationService.validateUpdate(currentVersion, editedVersion);

    boolean isOnlyValidityChanged = lineV3ShorteningService.isOnlyValidityChanged(currentVersion, editedVersion);
    boolean isShortening = lineV3ShorteningService.isShortening(currentVersion, editedVersion);

    if (isOnlyValidityChanged && isShortening) {
      shortenMembers(currentVersion, editedVersion);
    }

    List<LineVersionV3> versionsAfterVersioning = updateVersion(currentVersion, editedVersion);
    if (currentVersion.getReferencedLineSlnid() == null) {
      lineVersionV3ValidationService.validateMembersWithinParentValidity(versionsAfterVersioning);
    }
    return versionsAfterVersioning;
  }

  private void shortenMembers(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    for (LineV3MemberShortening memberShortening : lineV3ShorteningService.checkAndPrepareToShortMembers(currentVersion,
        editedVersion)) {
      updateVersion(memberShortening.currentVersion(), memberShortening.editedVersion());
    }
  }

  private List<LineVersionV3> updateVersion(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    lineVersionV3Repository.incrementVersion(currentVersion.getSlnid());
    if (!currentVersion.getVersion().equals(editedVersion.getVersion())) {
      throw new StaleObjectStateException(LineVersionV3.class.getSimpleName(), "version");
    }

    editedVersion.setSlnid(currentVersion.getSlnid());
    editedVersion.setReferencedLineSlnid(currentVersion.getReferencedLineSlnid());
    editedVersion.setLineType(currentVersion.getLineType());

    List<LineVersionV3> currentVersions = findLineVersions(currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(currentVersion,
        editedVersion, currentVersions);
    versionableService.doNotAllowGaps(versionedObjects);

    versionableService.applyVersioning(LineVersionV3.class, versionedObjects, this::save, this::deleteById);

    List<LineVersionV3> versionsAfterVersioning = findLineVersions(currentVersion.getSlnid());
    lineVersionV3ValidationService.validateLineAfterVersioningBusinessRule(versionsAfterVersioning);
    return versionsAfterVersioning;
  }

  private LineVersionV3 save(LineVersionV3 version) {
    version.setStatus(Status.VALIDATED);
    lineVersionV3ValidationService.validatePreconditionBusinessRules(version);
    return lineVersionV3Repository.saveAndFlush(version);
  }

  private void deleteById(long id) {
    if (!lineVersionV3Repository.existsById(id)) {
      throw new IdNotFoundException(id);
    }
    lineVersionV3Repository.deleteById(id);
    lineVersionV3Repository.flush();
  }

  public List<LineVersionModelV3> toModels(List<LineVersionV3> versions) {
    Map<String, List<LineVersionV3>> parentsBySlnid = loadParents(versions);
    return versions.stream()
        .map(version -> LineVersionV3Mapper.toModel(version, findParent(parentsBySlnid, version)))
        .toList();
  }

  private Map<String, List<LineVersionV3>> loadParents(List<LineVersionV3> versions) {
    Set<String> referencedSlnids = versions.stream()
        .map(LineVersionV3::getReferencedLineSlnid)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    if (referencedSlnids.isEmpty()) {
      return Map.of();
    }
    return lineVersionV3Repository.findAllBySlnidIn(referencedSlnids).stream()
        .collect(Collectors.groupingBy(LineVersionV3::getSlnid));
  }

  private static LineVersionV3 findParent(Map<String, List<LineVersionV3>> parentsBySlnid, LineVersionV3 version) {
    String referencedLineSlnid = version.getReferencedLineSlnid();
    if (referencedLineSlnid == null) {
      return null;
    }
    return selectParentVersion(parentsBySlnid.get(referencedLineSlnid));
  }

  private static LineVersionV3 selectParentVersion(List<LineVersionV3> parentVersions) {
    if (parentVersions == null || parentVersions.isEmpty()) {
      return null;
    }
    return OverviewDisplayBuilder.getPrioritizedVersion(parentVersions.stream()
        .sorted(Comparator.comparing(LineVersionV3::getValidFrom))
        .toList());
  }

}
