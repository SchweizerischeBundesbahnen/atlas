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
     * Documentation at CreateNewServicePointStatusDecision.puml and UpdateNewServicePointStatusDecision.puml
     */
    private Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion) {
        boolean isStopPoint = newServicePointVersion.isStopPoint();
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isSwissLocation = isSPLocatedInSwitzerland(newServicePointVersion);
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) + 1 > VALIDITY_IN_DAYS;

        return isSwissCountryCode && isStopPoint && isSwissLocation && isValidityLongEnough ? Status.DRAFT : Status.VALIDATED;
    }

    private boolean isSPLocatedInSwitzerland(ServicePointVersion newServicePointVersion) {
        if (isGeolocationOrCountryNull(newServicePointVersion)) return false;
        ServicePointGeolocation servicePointGeolocation = newServicePointVersion.getServicePointGeolocation();
        return servicePointGeolocation.getCountry().equals(Country.SWITZERLAND);
    }

    private static boolean isGeolocationOrCountryNull(ServicePointVersion newServicePointVersion) {
        return newServicePointVersion.getServicePointGeolocation() == null
                || newServicePointVersion.getServicePointGeolocation().getCountry() == null;
    }

    private boolean isNameChanged(ServicePointVersion newServicePointVersion,
                                  ServicePointVersion currentServicePointVersion) {
        return !newServicePointVersion.getDesignationOfficial().equals(currentServicePointVersion.getDesignationOfficial());
    }

    private boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion,
                                                        ServicePointVersion currentServicePointVersion) {
        return newServicePointVersion.isStopPoint() && !currentServicePointVersion.isStopPoint();
    }

    /**
     * Documentation at ServicePointStatusScenarios.md
     */
    public Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
                                           Optional<ServicePointVersion> currentServicePointVersion,
                                           List<ServicePointVersion> servicePointVersions) {
        if (currentServicePointVersion.isEmpty()) {
            // Create Scenario: Scenario when we create completely new StopPoint (1)
            return setStatusForStopPoint(newServicePointVersion, null, "Deciding on ServicePoint.Status when creating new StopPoint={}");
        } else {

            if (
                isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion.get()) ||
                    isTimeslotChangedFromNotValidEnoughToValidEnough(newServicePointVersion, currentServicePointVersion.get()) ||
                isGeolocationChangedBackToSwitzerland(newServicePointVersion, currentServicePointVersion.get())
//                    || isNewlyIntroducedVersionIsolated(newServicePointVersion, servicePointVersions)
                    || findIsolatedOrTouchingServicePointVersion(newServicePointVersion, servicePointVersions)
            ) {
                return setStatusForStopPoint(newServicePointVersion, currentServicePointVersion.get(),
                    "Deciding on ServicePoint.Status when update scenario where newServicePointVersion={} and currentServicePointVersion={}.");
            }
            // Update Scenario: extension of version with the same name (7, 14, 16, 17)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())
                    && isThereOverlappingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
                return setStatusAsInPreviousVersionOrToValidated(newServicePointVersion, currentServicePointVersion,
                        "Deciding on ServicePoint.Status when updating where, newServicePointVersion={}, and currentServicePointVersion={}. " +
                                         "DesignationOfficial name is changed, but there are exisiting touching versions with the same name");
            }
            // Update Scenario: Scenario update StopPoint with Name Change (covered cases: with gap, update on one part of existing version,
            // update on the whole version, update over 2 versions, extension) (4, 5, 6, 8, 9, 10, 11, 12, 13, 18)
            // Update Scenario: Scenario update from servicePoint to stopPoint (2, 3). Or scenario update when new ServicePointVersion is isolated (19).
            // Update Scenario: Scenario update when previous version is DRAFT (20). Scenario update from wrong Geolocation outside of Switzerland to geolocation inside of Switzerland (21)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get()) &&
                findPreviousVersionOnTheSameTimeslot(newServicePointVersion, servicePointVersions).isPresent()
            ) {
                return setStatusForStopPoint(newServicePointVersion, currentServicePointVersion.get(),
                        "Deciding on ServicePoint.Status when update scenario where newServicePointVersion={} and currentServicePointVersion={}.");
            }
        }
        // (15)
        return setStatusAsInPreviousVersionOrToValidated(newServicePointVersion, currentServicePointVersion,
                "Deciding on ServicePoint.Status when updating where, newServicePointVersion={}, and currentServicePointVersion={}. Status will be set to Validated per default.");
    }

    private Status setStatusForStopPoint(ServicePointVersion newServicePointVersion,
                                         ServicePointVersion currentServicePointVersion,
                                         String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
        return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }

    private Status setStatusAsInPreviousVersionOrToValidated(ServicePointVersion newServicePointVersion,
                                                  Optional<ServicePointVersion> currentServicePointVersion,
                                                  String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
        return currentServicePointVersion.map(ServicePointVersion::getStatus).orElse(Status.VALIDATED);
    }

    private Optional<ServicePointVersion> findPreviousVersionOnTheSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                               List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .filter(currentServicePointVersion -> (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        && !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        && (isNameChanged(newServicePointVersion, currentServicePointVersion))
                )
                .findFirst();
    }

    // Scenario where newServicePointVersion, has new name. And validity of newServicePointVersion is isolated or is extending (touching, but not overlapping) one of Existing servicePointVersions
    private boolean findIsolatedOrTouchingServicePointVersion(ServicePointVersion newServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        ServicePointVersion lastExistingServicePointVersion = servicePointVersions.isEmpty() ?
            null : servicePointVersions.get(servicePointVersions.size() - 1);
        ServicePointVersion firstExistingServicePointVersion = servicePointVersions.isEmpty() ?
            null : servicePointVersions.get(0);

      return lastExistingServicePointVersion != null &&
          lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom()) ||
          firstExistingServicePointVersion != null &&
              firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo());
    }






    private boolean isTimeslotChangedFromNotValidEnoughToValidEnough(ServicePointVersion newServicePointVersion,
        ServicePointVersion currentServicePointVersion) {
        long diffForCurrentVersion = ChronoUnit.DAYS.between(currentServicePointVersion.getValidFrom(), currentServicePointVersion.getValidTo());
        long diffForNewVersion = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo());
        return diffForCurrentVersion <= VALIDITY_IN_DAYS && diffForNewVersion > VALIDITY_IN_DAYS;
    }

    private boolean isGeolocationChangedBackToSwitzerland(ServicePointVersion newServicePointVersion,
        ServicePointVersion currentServicePointVersion) {
        if (isGeolocationOrCountryNull(newServicePointVersion)) {
            return false;
        }
        return isNewServicePointWithSwissGeolocation(newServicePointVersion)
            && isExistingServicePointWithAbroadOrNoGeolocation(currentServicePointVersion);
    }

    private static boolean isExistingServicePointWithAbroadOrNoGeolocation(ServicePointVersion currentServicePointVersion) {
        return isGeolocationOrCountryNull(currentServicePointVersion) ||
            !isNewServicePointWithSwissGeolocation(currentServicePointVersion);
    }

    private static boolean isNewServicePointWithSwissGeolocation(ServicePointVersion newServicePointVersion) {
        return Objects.equals(newServicePointVersion.getServicePointGeolocation().getCountry().getUicCode(),
            Country.SWITZERLAND.getUicCode());
    }

//    boolean isNewlyIntroducedVersionIsolated(ServicePointVersion newServicePointVersion,
//        List<ServicePointVersion> servicePointVersions) {
//        return !hasBorderDateOverlap(newServicePointVersion, servicePointVersions) &&
//        !hasOverlapWithExistingVersions(newServicePointVersion, servicePointVersions);
//    }
//
//    private boolean hasOverlapWithExistingVersions(ServicePointVersion newServicePointVersion,
//        List<ServicePointVersion> servicePointVersions) {
//        return servicePointVersions.stream()
//            .anyMatch(existing -> hasOverlap(existing, newServicePointVersion));
//    }
//
//    private boolean hasBorderDateOverlap(ServicePointVersion newServicePointVersion,
//        List<ServicePointVersion> servicePointVersions) {
//        return servicePointVersions
//            .stream()
//            .anyMatch(servicePointVersion ->
//                datesAreEqual(servicePointVersion.getValidFrom(), newServicePointVersion.getValidFrom())
//                    || datesAreEqual(servicePointVersion.getValidFrom(), newServicePointVersion.getValidTo())
//                    || datesAreEqual(servicePointVersion.getValidTo(), newServicePointVersion.getValidFrom())
//                    || datesAreEqual(servicePointVersion.getValidTo(), newServicePointVersion.getValidTo()));
//    }
//
//    private boolean datesAreEqual(LocalDate date1, LocalDate date2) {
//        return date1.equals(date2);
//    }

    private boolean isThereOverlappingVersionWithTheSameName(ServicePointVersion newServicePointVersion,
        List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions.stream()
            .filter(existing -> !isNameChanged(existing, newServicePointVersion))
            .anyMatch(existing -> hasOverlap(existing, newServicePointVersion));
    }

    private boolean hasOverlap(ServicePointVersion version1, ServicePointVersion version2) {
        return version1 != null && version2 != null &&
            version1.getValidFrom().isBefore(version2.getValidTo()) &&
            version1.getValidTo().isAfter(version2.getValidFrom());
    }

}
