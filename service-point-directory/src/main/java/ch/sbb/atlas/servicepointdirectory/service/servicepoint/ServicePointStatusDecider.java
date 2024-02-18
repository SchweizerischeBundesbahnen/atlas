package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointStatusDecider {

    private static final Long VALIDITY_IN_DAYS = 60L;

    /**
     * Documentation at ServicePointStatusScenarios.md
     */
    public Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
        Optional<ServicePointVersion> currentServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        if (currentServicePointVersion.isEmpty()) {
            logMessage(null,  newServicePointVersion,
                "Deciding on ServicePoint.Status when creating new StopPoint={}.");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
        }
        ServicePointVersion currentVersion = currentServicePointVersion.get();
        if (isChangeFromServicePointToStopPoint(newServicePointVersion, currentVersion)
            || isTimeslotChangedFromNotValidEnoughToValidEnough(newServicePointVersion, currentVersion)
            || hasGeolocationChangedBackToSwitzerland(newServicePointVersion, currentVersion)
            || isIsolatedOrTouchingServicePointVersion(newServicePointVersion, servicePointVersions)) {
            logMessage(currentVersion, newServicePointVersion,"Deciding on ServicePoint.Status when update where "
                + "currentServicePointVersion={}, existing versions servicePointVersions={} and newServicePointVersion={}.");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
        }
        if (hasNameChanged(newServicePointVersion, currentVersion)
            && isThereOverlappingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
            logMessage(currentVersion, newServicePointVersion, "Deciding on ServicePoint.Status "
                + "newServicePointVersion={}, and currentServicePointVersion={}. DesignationOfficial name is changed.");
            return setStatusAsInPreviousVersionOrToValidated(currentServicePointVersion);
        }
        if (hasNameChanged(newServicePointVersion, currentVersion)
            && hasVersionOnTheSameTimeslotWithDifferentName(newServicePointVersion, servicePointVersions)) {
            logMessage(currentVersion, newServicePointVersion, "Deciding on ServicePoint.Status "
                + "when update scenario where currentServicePointVersion={}, and newServicePointVersion={}.");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
        }
        logMessage(currentVersion, newServicePointVersion,
            "Deciding on ServicePoint.Status when updating where currentServicePointVersion={}, and "
                + "newServicePointVersion={}. Status will be set as in previous Version or to Validated per default.");
        return setStatusAsInPreviousVersionOrToValidated(currentServicePointVersion);
    }

    /**
     * Documentation at CreateNewServicePointStatusDecision.puml and UpdateNewServicePointStatusDecision.puml
     */
    private Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion) {
        boolean isStopPoint = newServicePointVersion.isStopPoint();
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isSwissLocation = isSPLocatedInSwitzerland(newServicePointVersion);
        boolean isValidityLongEnough = calculateDiffBetweenTwoDatesAndAddOne(newServicePointVersion) > VALIDITY_IN_DAYS;

        return isSwissCountryCode && isStopPoint && isSwissLocation && isValidityLongEnough ? Status.DRAFT : Status.VALIDATED;
    }

    private void logMessage(ServicePointVersion currentServicePointVersion, ServicePointVersion newServicePointVersion,
        String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
    }

    private boolean isSPLocatedInSwitzerland(ServicePointVersion newServicePointVersion) {
        if (isGeolocationOrCountryNull(newServicePointVersion)) {
            return false;
        }
        ServicePointGeolocation servicePointGeolocation = newServicePointVersion.getServicePointGeolocation();
        return servicePointGeolocation.getCountry().equals(Country.SWITZERLAND);
    }

    private static boolean isGeolocationOrCountryNull(ServicePointVersion newServicePointVersion) {
        return newServicePointVersion.getServicePointGeolocation() == null
            || newServicePointVersion.getServicePointGeolocation().getCountry() == null;
    }

    private boolean hasNameChanged(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return !newServicePointVersion.getDesignationOfficial().equals(currentServicePointVersion.getDesignationOfficial());
    }

    private boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion,
        ServicePointVersion currentServicePointVersion) {
        return newServicePointVersion.isStopPoint() && !currentServicePointVersion.isStopPoint();
    }

    private Status setStatusAsInPreviousVersionOrToValidated(Optional<ServicePointVersion> currentServicePointVersion) {
        return currentServicePointVersion.map(ServicePointVersion::getStatus).orElse(Status.VALIDATED);
    }

    private boolean hasVersionOnTheSameTimeslotWithDifferentName(ServicePointVersion newServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions.stream()
            .anyMatch(existing ->
                !existing.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                    && !existing.getValidFrom().isAfter(newServicePointVersion.getValidFrom())
                    && hasNameChanged(newServicePointVersion, existing));
    }

    /** Scenario where newServicePointVersion, has new name. And validity of newServicePointVersion is isolated or
    * is extending (touching, but not overlapping) one of Existing servicePointVersions
    **/
    private boolean isIsolatedOrTouchingServicePointVersion(ServicePointVersion newServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        ServicePointVersion lastExistingServicePointVersion = servicePointVersions.isEmpty() ?
            null : servicePointVersions.get(servicePointVersions.size() - 1);
        ServicePointVersion firstExistingServicePointVersion = servicePointVersions.isEmpty() ?
            null : servicePointVersions.get(0);

      return lastExistingServicePointVersion != null
          && lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
          || firstExistingServicePointVersion != null
          && firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo());
    }

    private boolean isTimeslotChangedFromNotValidEnoughToValidEnough(ServicePointVersion newServicePointVersion,
        ServicePointVersion currentServicePointVersion) {
        long diffForCurrentVersion = calculateDiffBetweenTwoDatesAndAddOne(currentServicePointVersion);
        long diffForNewVersion = calculateDiffBetweenTwoDatesAndAddOne(newServicePointVersion);
        return diffForCurrentVersion <= VALIDITY_IN_DAYS && diffForNewVersion > VALIDITY_IN_DAYS;
    }

    private static long calculateDiffBetweenTwoDatesAndAddOne(ServicePointVersion newServicePointVersion) {
        return ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) + 1;
    }

    private boolean hasGeolocationChangedBackToSwitzerland(ServicePointVersion newServicePointVersion,
        ServicePointVersion currentServicePointVersion) {
        if (isGeolocationOrCountryNull(newServicePointVersion)) {
            return false;
        }
        return isNewServicePointWithSwissGeolocation(newServicePointVersion)
            && isExistingServicePointWithAbroadOrNoGeolocation(currentServicePointVersion);
    }

    private static boolean isExistingServicePointWithAbroadOrNoGeolocation(ServicePointVersion currentServicePointVersion) {
        return isGeolocationOrCountryNull(currentServicePointVersion)
            || !isNewServicePointWithSwissGeolocation(currentServicePointVersion);
    }

    private static boolean isNewServicePointWithSwissGeolocation(ServicePointVersion newServicePointVersion) {
        return Objects.equals(newServicePointVersion.getServicePointGeolocation().getCountry().getUicCode(),
            Country.SWITZERLAND.getUicCode());
    }

    /** Scenario where newServicePointVersion, has the same name as previoius one.
     * And validity of newServicePointVersion is going over existing version with the different name.
     **/
    private boolean isThereOverlappingVersionWithTheSameName(ServicePointVersion newServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions.stream()
            .filter(existing -> !hasNameChanged(existing, newServicePointVersion))
            .anyMatch(existing -> hasOverlap(existing, newServicePointVersion));
    }

    private boolean hasOverlap(ServicePointVersion version1, ServicePointVersion version2) {
        return version1 != null && version2 != null &&
            version1.getValidFrom().isBefore(version2.getValidTo()) &&
            version1.getValidTo().isAfter(version2.getValidFrom());
    }

}
