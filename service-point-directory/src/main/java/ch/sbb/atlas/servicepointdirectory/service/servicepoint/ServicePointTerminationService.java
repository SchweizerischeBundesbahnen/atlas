package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedException;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicePointTerminationService {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  public void checkTerminationAllowed(ServicePointVersion editedVersion, List<ServicePointVersion> currentVersions,
      List<VersionedObject> versionedObjects) {
    DateRange preUpdateRange = getPreUpdateRange(currentVersions);
    DateRange postUpdateRange = getPostUpdateRange(versionedObjects);

    boolean isTermination = isTermination(editedVersion, currentVersions, preUpdateRange, postUpdateRange);

    if (isTermination && !businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)) {
      throw new TerminationNotAllowedException(editedVersion);
    }
  }

  private static boolean isTermination(ServicePointVersion editedVersion, List<ServicePointVersion> currentVersions,
      DateRange preUpdateRange, DateRange postUpdateRange) {
    if (postUpdateRange.getFrom().isAfter(preUpdateRange.getFrom())) {
      return currentVersions.stream()
          .filter(i -> i.getValidFrom().isBefore(editedVersion.getValidFrom()))
          .anyMatch(ServicePointVersion::isStopPoint);
    }
    if (postUpdateRange.getTo().isBefore(preUpdateRange.getTo())) {
      return currentVersions.stream()
          .filter(i -> i.getValidTo().isAfter(editedVersion.getValidTo()))
          .anyMatch(ServicePointVersion::isStopPoint);
    }
    return false;
  }

  private DateRange getPreUpdateRange(List<ServicePointVersion> currentVersions) {
    List<ServicePointVersion> sortedVersions = currentVersions.stream()
        .sorted(Comparator.comparing(ServicePointVersion::getValidFrom)).toList();
    LocalDate preUpdateRangeFrom = sortedVersions.get(0).getValidFrom();
    LocalDate preUpdateRangeTo = sortedVersions.get(currentVersions.size() - 1).getValidTo();

    return DateRange.builder().from(preUpdateRangeFrom).to(preUpdateRangeTo).build();
  }

  private DateRange getPostUpdateRange(List<VersionedObject> versionedObjects) {
    List<VersionedObject> sortedObjects = versionedObjects.stream().sorted(Comparator.comparing(VersionedObject::getValidFrom))
        .toList();
    LocalDate postUpdateRangeFrom = sortedObjects.get(0).getValidFrom();
    LocalDate postUpdateRangeTo = sortedObjects.get(versionedObjects.size() - 1).getValidTo();

    return DateRange.builder().from(postUpdateRangeFrom).to(postUpdateRangeTo).build();
  }

}
