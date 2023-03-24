package ch.sbb.line.directory.validation;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.user.administration.security.UpdateAffectedVersionLocator;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.exception.ForbiddenDueToInReviewException;
import ch.sbb.line.directory.exception.LineInReviewValidationException;
import ch.sbb.line.directory.exception.MergeOrSplitInReviewVersionException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineUpdateValidationService {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  public void validateLineForUpdate(LineVersion currentVersion, LineVersion editedVersion, List<LineVersion> currentVersions) {
    onlySupervisorMayEditVersionInReview(editedVersion, currentVersions);
    if (currentVersion.getStatus() == Status.IN_REVIEW) {
      typeAndTimeperiodOfLineInReviewMayNotChange(currentVersion, editedVersion);
    }
  }

  private void onlySupervisorMayEditVersionInReview(LineVersion editedVersion, List<LineVersion> currentVersions) {
    List<LineVersion> updateAffectedCurrentVersions = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(editedVersion,
        currentVersions);
    boolean inReviewVersionAffected = updateAffectedCurrentVersions.stream()
        .anyMatch(lineVersion -> lineVersion.getStatus() == Status.IN_REVIEW);
    if (inReviewVersionAffected && !businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)) {
      throw new ForbiddenDueToInReviewException();
    }
  }

  private void typeAndTimeperiodOfLineInReviewMayNotChange(LineVersion currentVersion, LineVersion editedVersion) {
    if (editedVersionTouchingCurrent(currentVersion, editedVersion)
        && (!currentVersion.getValidFrom().isEqual(editedVersion.getValidFrom())
        || !currentVersion.getValidTo().isEqual(editedVersion.getValidTo())
        || currentVersion.getLineType() != editedVersion.getLineType())) {
      throw new LineInReviewValidationException();
    }
  }

  private static boolean editedVersionTouchingCurrent(LineVersion currentVersion, LineVersion editedVersion) {
    return !editedVersionNotTouchingCurrent(currentVersion, editedVersion);
  }

  private static boolean editedVersionNotTouchingCurrent(LineVersion currentVersion, LineVersion editedVersion) {
    return editedVersion.getValidFrom().isAfter(currentVersion.getValidTo())
        || editedVersion.getValidTo().isBefore(currentVersion.getValidFrom());
  }

  public void validateVersioningNotAffectingReview(List<LineVersion> currentVersions, List<VersionedObject> versionedObjects) {
    Set<VersioningAction> mergeOrSplitIndicatingActions = Set.of(VersioningAction.NEW, VersioningAction.DELETE);
    boolean mergeOrSplitWillBePerformed = versionedObjects.stream()
        .anyMatch(versionedObject -> mergeOrSplitIndicatingActions.contains(versionedObject.getAction()));
    if (mergeOrSplitWillBePerformed) {
      List<VersionedObject> objectsWithStatusInReview = getObjectsWithStatusInReview(currentVersions, versionedObjects);
      if (!objectsWithStatusInReview.stream()
          .allMatch(versionedObject -> versionedObject.getAction() == VersioningAction.NOT_TOUCHED)) {
        throw new MergeOrSplitInReviewVersionException();
      }
    }
  }

  private static List<VersionedObject> getObjectsWithStatusInReview(List<LineVersion> currentVersions,
      List<VersionedObject> versionedObjects) {
    Set<Long> lineVersionIdsInReview =
        currentVersions.stream().filter(i -> i.getStatus() == Status.IN_REVIEW).map(LineVersion::getId)
            .collect(Collectors.toSet());

    return versionedObjects.stream().filter(versionedObject ->
        lineVersionIdsInReview.contains(versionedObject.getEntity().getId())).toList();
  }
}
