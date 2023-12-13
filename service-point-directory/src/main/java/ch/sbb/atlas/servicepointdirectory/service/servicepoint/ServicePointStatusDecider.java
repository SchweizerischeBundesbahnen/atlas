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

    private static final String LOG_MESSAGE_BEGINNING = "Deciding on ServicePoint.Status when updating from stopPoint, currentServicePointVersion={} to stopPoint, ";

    /**
     * Documentation at CreateNewServicePointStatusDecision.puml and UpdateNewServicePointStatusDecision.puml
     */
    private Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion) {
        boolean isStopPoint = newServicePointVersion.isStopPoint();
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isSwissLocation = isLocatedInSwitzerland(newServicePointVersion);
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) > VALIDITY_IN_DAYS;

        return isSwissCountryCode && isStopPoint && isSwissLocation && isValidityLongEnough ? Status.DRAFT : Status.VALIDATED;
    }

    private boolean isLocatedInSwitzerland(ServicePointVersion newServicePointVersion) {
        if (newServicePointVersion.getServicePointGeolocation() == null) {
            return false;
        }
        ServicePointGeolocation servicePointGeolocation = newServicePointVersion.getServicePointGeolocation();
        return servicePointGeolocation.getCountry().equals(Country.SWITZERLAND);
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
            // Scenario when we create completely new StopPoint
            return setStatusForNewlyCreatedStopPoint(newServicePointVersion);
        } else {
            // Scenario update from servicePoint to stopPoint (2, 3). Or scenario update when new ServicePointVersion is isolated (19).
            if (isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion.get())
                    || checkIfVersionIsIsolated(newServicePointVersion, servicePointVersions)) {
                return setStatusForStopPoint(newServicePointVersion, currentServicePointVersion.get(),
                        "Deciding on ServicePoint.Status when new StopPoint is isolated or updating from servicePoint, currentServicePointVersion={} to stopPoint " + "newServicePointVersion={}.");
            }
            // Scenario extension of version with the same name (16, 17, 7)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())
                    && isThereTouchingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
                return setStatusPerDefaultAsValidated(newServicePointVersion, currentServicePointVersion,
                        "newServicePointVersion={}. DesignationOfficial name is changed, but there are exisiting touching versions with the same name");
            }
            // Scenario update StopPoint with Name Change (covered cases: with gap, update on one part of existing version, update on whole version, update over 2 versions, extension), (4, 5, 6, 8, 9, 10, 11, 12, 13, 18)
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())
                    && findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions).isPresent()
                    || findIsolatedOrConsequentServicePointVersion(newServicePointVersion, servicePointVersions).isPresent()) {
                return setStatusForStopPoint(newServicePointVersion, currentServicePointVersion.get(),
                        LOG_MESSAGE_BEGINNING + "newServicePointVersion={}. DesignationOfficial name is changed");
            }
        }
        return setStatusPerDefaultAsValidated(newServicePointVersion, currentServicePointVersion,
                "newServicePointVersion={}. Status will be set to Validated.");
    }

    private Status setStatusForNewlyCreatedStopPoint(ServicePointVersion newServicePointVersion) {
        return setStatusForStopPoint(newServicePointVersion, null, "Deciding on ServicePoint.Status when creating new StopPoint={}");
    }

    private Status setStatusForStopPoint(ServicePointVersion newServicePointVersion,
                                         ServicePointVersion currentServicePointVersion,
                                         String logMessage) {
        log.info(logMessage, currentServicePointVersion, newServicePointVersion);
        return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
    }

    private Status setStatusPerDefaultAsValidated(ServicePointVersion newServicePointVersion,
                                                  Optional<ServicePointVersion> currentServicePointVersion,
                                                  String lastPartOfLogMessage) {
        log.info(LOG_MESSAGE_BEGINNING + lastPartOfLogMessage, currentServicePointVersion, newServicePointVersion);
        return Status.VALIDATED;
    }

    private Optional<ServicePointVersion> findPreviousVersionOnSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                            List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .filter(currentServicePointVersion -> (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        && !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        && (isNameChanged(newServicePointVersion, currentServicePointVersion)))
                .findFirst();
    }

    // Scenario where newServicePointVersion, has new name. And validity of newServicePointVersion is isolated or is extending (touching, but not overlapping) one of Existing servicePointVersions
    private Optional<ServicePointVersion> findIsolatedOrConsequentServicePointVersion(ServicePointVersion newServicePointVersion,
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

    boolean checkIfVersionIsIsolated(ServicePointVersion newServicePointVersion,
                                     List<ServicePointVersion> servicePointVersions) {
        if (checkIfSomeDateEqual(newServicePointVersion, servicePointVersions)) {
            return false;
        }
        return !checkIfSomeOverlap(newServicePointVersion, servicePointVersions);
    }

    private boolean checkIfSomeOverlap(ServicePointVersion newServicePointVersion,
                                       List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions
                .stream()
                .anyMatch(servicePointVersion -> servicePointVersion.getValidFrom().isBefore(newServicePointVersion.getValidTo())
                        && newServicePointVersion.getValidFrom().isBefore(servicePointVersion.getValidTo()));
    }

    private boolean checkIfSomeDateEqual(ServicePointVersion newServicePointVersion,
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

    private boolean isThereTouchingVersionWithTheSameName(ServicePointVersion newServicePointVersion,
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
