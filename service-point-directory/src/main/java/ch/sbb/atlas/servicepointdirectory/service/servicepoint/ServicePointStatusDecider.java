package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ServicePointStatusDecider {

  private static final Long VALIDITY_IN_DAYS = 60L;

  /**
   * Documentation at ServicePointStatusScenarios.md
   */
  public static Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
      Optional<ServicePointVersion> currentServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    if (currentServicePointVersion.isEmpty()) {
      logMessage(null, newServicePointVersion,
          "Deciding on ServicePoint.Status when creating new StopPoint={}.");
      return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }
    ServicePointVersion currentVersion = calculateCurrentVersion(servicePointVersions, newServicePointVersion,
        currentServicePointVersion.get());
    if (isChangeFromServicePointToStopPoint(newServicePointVersion, currentVersion)
        || isTimeslotChangedFromNotValidEnoughToValidEnough(newServicePointVersion, currentVersion)
        || hasGeolocationChangedBackToSwitzerland(newServicePointVersion, currentVersion)
        || isIsolatedOrTouchingServicePointVersion(newServicePointVersion, servicePointVersions)) {
      logMessage(currentVersion, newServicePointVersion, "Deciding on ServicePoint.Status when update where "
          + "currentServicePointVersion={}, existing versions servicePointVersions={} and newServicePointVersion={}.");
      return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }
    if (hasNameChanged(newServicePointVersion, currentVersion)
        && isThereOverlappingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
      logMessage(currentVersion, newServicePointVersion, "Deciding on ServicePoint.Status "
          + "newServicePointVersion={}, and currentServicePointVersion={}. DesignationOfficial name is changed.");
      return getStatusFromCurrentVersion(currentVersion);
    }
    if (hasNameChanged(newServicePointVersion, currentVersion)
        && hasVersionOnTheSameTimeslotWithDifferentName(newServicePointVersion, servicePointVersions)
        && currentVersion.getStatus() != Status.IN_REVIEW) {
      logMessage(currentVersion, newServicePointVersion, "Deciding on ServicePoint.Status "
          + "when update scenario where currentServicePointVersion={}, and newServicePointVersion={}.");
      return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }
    logMessage(currentVersion, newServicePointVersion,
        "Deciding on ServicePoint.Status when updating where currentServicePointVersion={}, and "
            + "newServicePointVersion={}. Status will be set as in previous Version or to Validated per default.");
    return getStatusFromCurrentVersion(currentVersion);
  }

  /**
   * Documentation at CreateNewServicePointStatusDecision.puml and UpdateNewServicePointStatusDecision.puml
   */
  private static Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion) {
    boolean isStoPointLocatedInSwiss = ServicePointHelper.isStopPointLocatedInSwitzerland(newServicePointVersion);
    boolean isValidityLongEnough = calculateDiffBetweenTwoDatesAndAddOne(newServicePointVersion) > VALIDITY_IN_DAYS;
    return isStoPointLocatedInSwiss && isValidityLongEnough ? Status.DRAFT : Status.VALIDATED;
  }

  private static void logMessage(ServicePointVersion currentVersion, ServicePointVersion newServicePointVersion,
      String logMessage) {
    log.info(logMessage, currentVersion, newServicePointVersion);
  }

  private static boolean hasNameChanged(ServicePointVersion newServicePointVersion, ServicePointVersion currentVersion) {
    return !newServicePointVersion.getDesignationOfficial().equals(currentVersion.getDesignationOfficial());
  }

  private static boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion,
      ServicePointVersion currentVersion) {
    return newServicePointVersion.isStopPoint() && !currentVersion.isStopPoint();
  }

  private static ServicePointVersion calculateCurrentVersion(List<ServicePointVersion> servicePointVersions,
      ServicePointVersion newVersion, ServicePointVersion currentVersion) {
    Optional<ServicePointVersion> overlappingVersion = servicePointVersions.stream()
        .filter(existing -> isOverlapping(existing, newVersion))
        .findFirst();
    return overlappingVersion.orElse(currentVersion);
  }

  private static Status getStatusFromCurrentVersion(ServicePointVersion currentVersion) {
    return currentVersion.getStatus();
  }

  private static boolean hasVersionOnTheSameTimeslotWithDifferentName(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    return servicePointVersions.stream()
        .anyMatch(existing -> isOverlapping(existing, newServicePointVersion)
            && hasNameChanged(newServicePointVersion, existing));
  }

  /**
   * Scenario where newServicePointVersion, has new name. And validity of newServicePointVersion is isolated or
   * is extending (touching, but not overlapping) one of Existing servicePointVersions
   **/
  static boolean isIsolatedOrTouchingServicePointVersion(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    ServicePointVersion lastExistingServicePointVersion = servicePointVersions.isEmpty() ?
        null : servicePointVersions.getLast();
    ServicePointVersion firstExistingServicePointVersion = servicePointVersions.isEmpty() ?
        null : servicePointVersions.getFirst();

    return lastExistingServicePointVersion != null
        && lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
        || firstExistingServicePointVersion != null
        && firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo());
  }

  private static boolean isTimeslotChangedFromNotValidEnoughToValidEnough(ServicePointVersion newServicePointVersion,
      ServicePointVersion currentVersion) {
    long diffForCurrentVersion = calculateDiffBetweenTwoDatesAndAddOne(currentVersion);
    long diffForNewVersion = calculateDiffBetweenTwoDatesAndAddOne(newServicePointVersion);
    return diffForCurrentVersion <= VALIDITY_IN_DAYS && diffForNewVersion > VALIDITY_IN_DAYS;
  }

  private static long calculateDiffBetweenTwoDatesAndAddOne(ServicePointVersion newServicePointVersion) {
    return ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) + 1;
  }

  private static boolean hasGeolocationChangedBackToSwitzerland(ServicePointVersion newServicePointVersion,
      ServicePointVersion currentVersion) {
    if (ServicePointHelper.isGeolocationOrCountryNull(newServicePointVersion)) {
      return false;
    }
    return isNewServicePointWithSwissGeolocation(newServicePointVersion)
        && isExistingServicePointWithAbroadOrNoGeolocation(currentVersion);
  }

  private static boolean isExistingServicePointWithAbroadOrNoGeolocation(ServicePointVersion currentVersion) {
    return ServicePointHelper.isGeolocationOrCountryNull(currentVersion)
        || !isNewServicePointWithSwissGeolocation(currentVersion);
  }

  private static boolean isNewServicePointWithSwissGeolocation(ServicePointVersion newServicePointVersion) {
    return Objects.equals(newServicePointVersion.getServicePointGeolocation().getCountry().getUicCode(),
        Country.SWITZERLAND.getUicCode());
  }

  /**
   * Scenario where newServicePointVersion, has the same name as previoius one.
   * And validity of newServicePointVersion is going over existing version with the different name.
   **/
  static boolean isThereOverlappingVersionWithTheSameName(ServicePointVersion newServicePointVersion,
      List<ServicePointVersion> servicePointVersions) {
    return servicePointVersions.stream()
        .filter(existing -> existing.getStatus().equals(Status.VALIDATED))
        .filter(existing -> !hasNameChanged(existing, newServicePointVersion))
        .anyMatch(existing -> isOverlapping(existing, newServicePointVersion));
  }

  private static boolean isOverlapping(ServicePointVersion version1, ServicePointVersion version2) {
    return version1 != null && version2 != null &&
        version1.getValidFrom().isBefore(version2.getValidTo()) &&
        version1.getValidTo().isAfter(version2.getValidFrom());
  }

}
