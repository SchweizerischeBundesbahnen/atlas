package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) > VALIDITY_IN_DAYS;

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
            // Update Scenario: extension of version with the same name (7, 14, 16, 17)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())
                    && isThereOverlappingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
                return setStatusPerDefaultAsValidated(newServicePointVersion, currentServicePointVersion,
                        "Deciding on ServicePoint.Status when updating where, newServicePointVersion={}, and currentServicePointVersion={}. " +
                                         "DesignationOfficial name is changed, but there are exisiting touching versions with the same name");
            }
            // Update Scenario: Scenario update StopPoint with Name Change (covered cases: with gap, update on one part of existing version,
            // update on the whole version, update over 2 versions, extension) (4, 5, 6, 8, 9, 10, 11, 12, 13, 18)
            // Update Scenario: Scenario update from servicePoint to stopPoint (2, 3). Or scenario update when new ServicePointVersion is isolated (19).
            // Update Scenario: Scenario update when previous version is DRAFT (20). Scenario update from wrong Geolocation outside of Switzerland to geolocation inside of Switzerland (21)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get()) && findPreviousVersionOnTheSameTimeslot(newServicePointVersion, servicePointVersions).isPresent()
                    || findIsolatedOrTouchingServicePointVersion(newServicePointVersion, servicePointVersions).isPresent()
                    || isPreviousVersionDraft(currentServicePointVersion.get())
                    || isGeolocationChangedFromAbroadToSwitzerland(newServicePointVersion, currentServicePointVersion.get())
                    || isTimeslotChangeFromLessThan60DaysToMoreThan60Days(newServicePointVersion, currentServicePointVersion.get())
                    || isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion.get())
                    || isVersionIsolated(newServicePointVersion, servicePointVersions)) {
                return setStatusForStopPoint(newServicePointVersion, currentServicePointVersion.get(),
                        "Deciding on ServicePoint.Status when update scenario where newServicePointVersion={} and currentServicePointVersion={}.");
            }
        }
        // (15)
        return setStatusPerDefaultAsValidated(newServicePointVersion, currentServicePointVersion,
                "Deciding on ServicePoint.Status when updating where, newServicePointVersion={}, and currentServicePointVersion={}. Status will be set to Validated per default.");
    }

    private Status setStatusForStopPoint(ServicePointVersion newServicePointVersion,
                                         ServicePointVersion currentServicePointVersion,
                                         String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
        return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }

    private Status setStatusPerDefaultAsValidated(ServicePointVersion newServicePointVersion,
                                                  Optional<ServicePointVersion> currentServicePointVersion,
                                                  String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
//        return Status.VALIDATED;
        return currentServicePointVersion.map(ServicePointVersion::getStatus).orElse(Status.VALIDATED);
    }

    private Optional<ServicePointVersion> findPreviousVersionOnTheSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                               List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .filter(currentServicePointVersion -> (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        && !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        && (isNameChanged(newServicePointVersion, currentServicePointVersion)))
                .findFirst();
    }

    // Scenario where newServicePointVersion, has new name. And validity of newServicePointVersion is isolated or is extending (touching, but not overlapping) one of Existing servicePointVersions
    private Optional<ServicePointVersion> findIsolatedOrTouchingServicePointVersion(ServicePointVersion newServicePointVersion,
                                                                                    List<ServicePointVersion> currentServicePointVersions) {
        ServicePointVersion lastExistingServicePointVersion = getLastOfExistingVersions(currentServicePointVersions);
        ServicePointVersion firstExistingServicePointVersion = getFirstOfExistingVersions(currentServicePointVersions);
        if (lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                || firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo())) {
            return currentServicePointVersions
                    .stream()
                    .filter(currentServicePointVersion -> (isNameChanged(newServicePointVersion, currentServicePointVersion)))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private boolean isPreviousVersionDraft(ServicePointVersion currentServicePointVersion) {
        return currentServicePointVersion.getStatus() == Status.DRAFT;
    }

    private boolean isTimeslotChangeFromLessThan60DaysToMoreThan60Days(ServicePointVersion newServicePointVersion,
                                                                ServicePointVersion currentServicePointVersion) {
        long diffForCurrentVersion = ChronoUnit.DAYS.between(currentServicePointVersion.getValidFrom(), currentServicePointVersion.getValidTo());
        long diffForNewVersion = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo());
        return diffForCurrentVersion <= VALIDITY_IN_DAYS && diffForNewVersion > VALIDITY_IN_DAYS;
    }

    private boolean isGeolocationChangedFromAbroadToSwitzerland(ServicePointVersion newServicePointVersion,
                                                                ServicePointVersion currentServicePointVersion) {
        if (isGeolocationOrCountryNull(newServicePointVersion)) {
            return false;
        }
        return Objects.equals(newServicePointVersion.getServicePointGeolocation().getCountry().getUicCode(), Country.SWITZERLAND.getUicCode())
                && (isGeolocationOrCountryNull(currentServicePointVersion) ||
                !Objects.equals(currentServicePointVersion.getServicePointGeolocation().getCountry().getUicCode(), Country.SWITZERLAND.getUicCode()));
    }

    boolean isVersionIsolated(ServicePointVersion newServicePointVersion,
                              List<ServicePointVersion> servicePointVersions) {
        if (checkIfSomeVersionFromOrToDatesAreEqual(newServicePointVersion, servicePointVersions)) {
            return false;
        }
        return !checkIfSomeVersionsOverlap(newServicePointVersion, servicePointVersions);
    }

    private boolean checkIfSomeVersionsOverlap(ServicePointVersion newServicePointVersion,
                                               List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions
                .stream()
                .anyMatch(servicePointVersion -> servicePointVersion.getValidFrom().isBefore(newServicePointVersion.getValidTo())
                        && newServicePointVersion.getValidFrom().isBefore(servicePointVersion.getValidTo()));
    }

    private boolean checkIfSomeVersionFromOrToDatesAreEqual(ServicePointVersion newServicePointVersion,
                                                            List<ServicePointVersion> servicePointVersionList) {
        return servicePointVersionList
                .stream()
                .anyMatch(servicePointVersion -> servicePointVersion.getValidFrom().equals(newServicePointVersion.getValidFrom())
                        || servicePointVersion.getValidFrom().equals(newServicePointVersion.getValidTo())
                        || servicePointVersion.getValidTo().equals(newServicePointVersion.getValidFrom())
                        || servicePointVersion.getValidTo().equals(newServicePointVersion.getValidTo()));
    }

    private ServicePointVersion getLastOfExistingVersions(List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .skip(currentServicePointVersions.size() - 1L).findFirst().orElseThrow();
    }

    private ServicePointVersion getFirstOfExistingVersions(List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions.stream().findFirst().orElseThrow();
    }

    private boolean isThereOverlappingVersionWithTheSameName(ServicePointVersion newServicePointVersion,
                                                             List<ServicePointVersion> currentServicePointVersions) {
        Optional<ServicePointVersion> found = currentServicePointVersions
                .stream()
                .filter(servicePointVersion -> !isNameChanged(newServicePointVersion, servicePointVersion))
                .filter(servicePointVersion -> !isChangeFromServicePointToStopPoint(newServicePointVersion, servicePointVersion))
                .filter(servicePointVersion -> checkOverlapping(servicePointVersion, newServicePointVersion))
                .findFirst();
        return found.isPresent();
    }

    private boolean checkOverlapping(ServicePointVersion existing, ServicePointVersion newOne) {
        if (existing == null) {
            return false;
        }
        return existing.getValidFrom().isBefore(newOne.getValidTo()) && existing.getValidTo().isAfter(newOne.getValidFrom());
    }

}
