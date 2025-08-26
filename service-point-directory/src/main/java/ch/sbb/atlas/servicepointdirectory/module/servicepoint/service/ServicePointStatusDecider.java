package ch.sbb.atlas.servicepointdirectory.module.servicepoint.service;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.helper.ServicePointHelper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Documentation at ServicePointStatusScenarios.md
 */
@Slf4j
@UtilityClass
public class ServicePointStatusDecider {

  private static final Long VALIDITY_IN_DAYS = 60L;

  public static Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
      Optional<ServicePointVersion> currentServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {

    if (!newServicePointVersion.getNumber().getCountry().equals(Country.SWITZERLAND)) {
      return Status.VALIDATED;
    }
    if (!newServicePointVersion.isStopPoint()) {
      return Status.VALIDATED;
    }

    if (currentServicePointVersion.isPresent()) {
      ServicePointVersion currentVersion = calculateCurrentVersion(servicePointVersions, newServicePointVersion,
          currentServicePointVersion.get());
      return calculateStatusWithCurrentVersion(newServicePointVersion, servicePointVersions, currentVersion);
    } else {
      return getStatusForVersionAccordingToValidityAndLocation(newServicePointVersion);
    }
  }

  private static ServicePointVersion calculateCurrentVersion(List<ServicePointVersion> servicePointVersions,
      ServicePointVersion newVersion, ServicePointVersion currentVersion) {
    return servicePointVersions.stream()
        .filter(existing -> DateRange.fromVersionable(existing).overlapsWith(DateRange.fromVersionable(newVersion)))
        .findFirst()
        .orElse(currentVersion);
  }

  private static Status calculateStatusWithCurrentVersion(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions,
      ServicePointVersion currentVersion) {
    if (!Set.of(Status.VALIDATED, Status.DRAFT).contains(currentVersion.getStatus())) {
      return currentVersion.getStatus();
    }

    boolean hasDesignationOfficialChanged = hasDesignationOfficialChanged(newServicePointVersion, currentVersion);
    boolean hasOverlappingVersionWithSameDesignationOfficial = hasOverlappingVersionWithSameDesignationOfficial(
        newServicePointVersion, servicePointVersions);
    if (hasDesignationOfficialChanged && hasOverlappingVersionWithSameDesignationOfficial) {
      return currentVersion.getStatus();
    }

    boolean currentVersionInStatusDraft = currentVersion.getStatus().equals(Status.DRAFT);
    boolean locationInSwitzerlandChanged = ServicePointHelper.isStopPointLocatedInSwitzerland(newServicePointVersion)
        != ServicePointHelper.isStopPointLocatedInSwitzerland(currentVersion);
    boolean validityChanged = hasValidSixtyDaysOrLess(newServicePointVersion) != hasValidSixtyDaysOrLess(currentVersion);
    boolean outsideOfCurrentVersionsRange = isNewVersionOutsideOfCurrentVersionsRange(newServicePointVersion,
        servicePointVersions);

    if (currentVersionInStatusDraft || hasDesignationOfficialChanged || locationInSwitzerlandChanged || validityChanged
        || outsideOfCurrentVersionsRange) {
      return getStatusForVersionAccordingToValidityAndLocation(newServicePointVersion);
    } else {
      return Status.VALIDATED;
    }
  }

  private static boolean hasDesignationOfficialChanged(ServicePointVersion newServicePointVersion,
      ServicePointVersion currentVersion) {
    return !newServicePointVersion.getDesignationOfficial().equals(currentVersion.getDesignationOfficial());
  }

  private static boolean hasOverlappingVersionWithSameDesignationOfficial(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    return servicePointVersions.stream()
        .filter(existing -> existing.getStatus().equals(Status.VALIDATED))
        .filter(existing -> !hasDesignationOfficialChanged(existing, newServicePointVersion))
        .anyMatch(
            existing -> DateRange.fromVersionable(existing).overlapsWith(DateRange.fromVersionable(newServicePointVersion)));
  }

  private static boolean isNewVersionOutsideOfCurrentVersionsRange(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    if (servicePointVersions.isEmpty()) {
      return false;
    }
    return servicePointVersions.getLast().getValidTo().isBefore(newServicePointVersion.getValidFrom()) ||
        servicePointVersions.getFirst().getValidFrom().isAfter(newServicePointVersion.getValidTo());
  }

  private static Status getStatusForVersionAccordingToValidityAndLocation(ServicePointVersion newServicePointVersion) {
    if (hasValidSixtyDaysOrLess(newServicePointVersion)) {
      return Status.VALIDATED;
    }
    if (!ServicePointHelper.isStopPointLocatedInSwitzerland(newServicePointVersion)) {
      return Status.VALIDATED;
    }
    return Status.DRAFT;
  }

  private static boolean hasValidSixtyDaysOrLess(ServicePointVersion newServicePointVersion) {
    return DateRange.fromVersionable(newServicePointVersion).getValidityInDays() <= VALIDITY_IN_DAYS;
  }
}
