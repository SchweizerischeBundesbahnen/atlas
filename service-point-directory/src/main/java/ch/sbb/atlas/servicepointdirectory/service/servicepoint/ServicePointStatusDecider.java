package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointStatusDecider {

    private final GeoReferenceService geoReferenceService;

    @Value("${validity-in-days}")
    private String validityInDays;

    private Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion) {
        boolean isStopPoint = newServicePointVersion.isStopPoint();
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isSwissLocation = isLocatedInSwitzerland(newServicePointVersion);
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) > Long.parseLong(validityInDays);

        return isSwissCountryCode &&
                isStopPoint &&
                isSwissLocation &&
                isValidityLongEnough ? Status.DRAFT : Status.VALIDATED;
    }

    private boolean isLocatedInSwitzerland(ServicePointVersion newServicePointVersion) {
        if (newServicePointVersion.getServicePointGeolocation() == null) {
            return false;
        }
        ServicePointGeolocation servicePointGeolocation = newServicePointVersion.getServicePointGeolocation();
        GeoReference geoReference = geoReferenceService.getGeoReference(servicePointGeolocation.asCoordinatePair());

        return geoReference.getCountry().equals(Country.SWITZERLAND);
    }

    private boolean isNameChanged(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return !newServicePointVersion.getDesignationOfficial().equals(currentServicePointVersion.getDesignationOfficial());
    }

    private boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return newServicePointVersion.isStopPoint() && !currentServicePointVersion.isStopPoint();
    }

    public Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
                                           Optional<ServicePointVersion> currentServicePointVersion,
                                           List<ServicePointVersion> servicePointVersions) {
        // case 1 new StopPoint, scenario 1
        if (currentServicePointVersion.isEmpty() && CollectionUtils.isEmpty(servicePointVersions)) {
            log.info("Deciding on ServicePoint.Status when creating new StopPoint={}", newServicePointVersion);
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
        }

        if (currentServicePointVersion.isPresent()) {
            // case 2 change from servicePoint to stopPoint, scenario 2, scenario 3
            if (isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion.get())) {
                log.info("Deciding on ServicePoint.Status when updating from servicePoint, currentServicePointVersion={} to stopPoint, " +
                        "newServicePointVersion={}", currentServicePointVersion.get(), newServicePointVersion);
                return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
            }

            // case 3 change from stopPoint to stopPoint, with or without name change
            if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())) {
                // cover extensions of version with the same name, then status is validated
                if (isThereTouchingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) { // scenario 16, scenario 17, scenario 7, scenario 14, scenario 15, (14 and 15 depend on which version we do update, might not enter here)
                    log.info("Deciding on ServicePoint.Status when updating from stopPoint, currentServicePointVersion={} to stopPoint, " +
                                    "newServicePointVersion={}. DesignationOfficial name is changed, but there are exisiting touching versions with the same name",
                            currentServicePointVersion, newServicePointVersion);
                    return Status.VALIDATED;
                }

                Optional<ServicePointVersion> servicePointVersion = findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions);
                log.info("Deciding on ServicePoint.Status when updating from stopPoint, currentServicePointVersion={} to stopPoint, " +
                                "newServicePointVersion={}. DesignationOfficial name is changed",
                        currentServicePointVersion, newServicePointVersion);
                // scenario 4, scenario 5, scenario 6, scenario 8, scenario 9, scenario 10, scenario 11, scenario 12, scenario 13, scenario 18
                if (servicePointVersion.isPresent()) return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
            }

            // case update with gap without name change, scenario 19
            if (checkIfVersionIsIsolated(newServicePointVersion, servicePointVersions)) {
                log.info("Deciding on ServicePoint.Status when updating from stopPoint, currentServicePointVersion={} to stopPoint, " +
                                "newServicePointVersion={}. DesignationOfficial name is the same, but there is a gap between existing and new version",
                        currentServicePointVersion, newServicePointVersion);
                return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion);
            }
        }

        log.info("Deciding on ServicePoint.Status when updating from stopPoint, currentServicePointVersion={} to stopPoint, " +
                        "newServicePointVersion={}. Status will be set to Validated.",
                currentServicePointVersion, newServicePointVersion);
        return Status.VALIDATED;
    }

    // for scenarios 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
    private Optional<ServicePointVersion> findPreviousVersionOnSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                            List<ServicePointVersion> currentServicePointVersions) {

        // this is for scenarios 12, 13, 18
        ServicePointVersion lastExistingServicePointVersion = getLastOfExistingVersions(currentServicePointVersions);
        ServicePointVersion firstExistingServicePointVersion = getFirstOfExistingVersions(currentServicePointVersions);
        if (lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                || firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo())) {
            return currentServicePointVersions
                    .stream()
                    .filter(currentServicePointVersion -> (isNameChanged(newServicePointVersion, currentServicePointVersion)
                            || isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
                    .findFirst();
        }
        return currentServicePointVersions
                .stream()
                .filter(currentServicePointVersion -> (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        && !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        && (isNameChanged(newServicePointVersion, currentServicePointVersion)
                        || isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
                .findFirst();
    }

    public boolean checkIfVersionIsIsolated(ServicePointVersion newServicePointVersion,
                                            List<ServicePointVersion> servicePointVersions) {
        if (checkIfSomeDateEqual(newServicePointVersion, servicePointVersions)) {
            return false;
        }
        return !checkIfSomeOverlap(newServicePointVersion, servicePointVersions);
    }

    public boolean checkIfSomeOverlap(ServicePointVersion newServicePointVersion, List<ServicePointVersion> servicePointVersions) {
        return servicePointVersions.stream()
                .anyMatch(servicePointVersion -> servicePointVersion.getValidFrom().isBefore(newServicePointVersion.getValidTo()) &&
                        newServicePointVersion.getValidFrom().isBefore(servicePointVersion.getValidTo()));
    }

    public boolean checkIfSomeDateEqual(ServicePointVersion newServicePointVersion, List<ServicePointVersion> servicePointVersionList) {
        return servicePointVersionList.stream()
                .anyMatch(servicePointVersion -> servicePointVersion.getValidFrom().equals(newServicePointVersion.getValidFrom())
                        || servicePointVersion.getValidFrom().equals(newServicePointVersion.getValidTo())
                        || servicePointVersion.getValidTo().equals(newServicePointVersion.getValidFrom())
                        || servicePointVersion.getValidTo().equals(newServicePointVersion.getValidTo()));
    }

    private ServicePointVersion getLastOfExistingVersions(List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .skip(currentServicePointVersions.size() - 1)
                .findFirst()
                .orElseThrow();
    }

    private ServicePointVersion getFirstOfExistingVersions(List<ServicePointVersion> currentServicePointVersions) {
        return currentServicePointVersions
                .stream()
                .findFirst()
                .orElseThrow();
    }

    private boolean isThereTouchingVersionWithTheSameName(ServicePointVersion newServicePointVersion, List<ServicePointVersion> currentServicePointVersions) {
        Optional<ServicePointVersion> found = currentServicePointVersions
                .stream()
                .filter(servicePointVersion -> !isNameChanged(newServicePointVersion, servicePointVersion))
                .filter(servicePointVersion -> !isChangeFromServicePointToStopPoint(newServicePointVersion, servicePointVersion))
                .filter(servicePointVersion -> checkOverlapping(servicePointVersion, newServicePointVersion))
                .findFirst();
        return found.isPresent();
    }

    private boolean checkOverlapping(ServicePointVersion existing, ServicePointVersion newOne) {
        if (existing == null) return false;
        return existing.getValidFrom().isBefore(newOne.getValidTo()) && existing.getValidTo().isAfter(newOne.getValidFrom());
    }

}
