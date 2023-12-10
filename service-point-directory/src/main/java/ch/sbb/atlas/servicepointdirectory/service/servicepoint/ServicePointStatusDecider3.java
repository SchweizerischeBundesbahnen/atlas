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

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointStatusDecider3 {

    private final GeoReferenceService geoReferenceService;

    @Value("${validity-in-days}")
    private String validityInDays;

    private Status calculateStatusAccordingToStatusDecisionAlgorithm(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        boolean isNameChanged = isNameChanged(newServicePointVersion, currentServicePointVersion);
        boolean isChangeFromServicePointToStopPoint = isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion);
        boolean isStopPoint = newServicePointVersion.isStopPoint();
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isSwissLocation = isLocatedInSwitzerland(newServicePointVersion);
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) > Long.parseLong(validityInDays);

        return isSwissCountryCode &&
                isStopPoint &&
                isSwissLocation &&
//                (isNameChanged || isChangeFromServicePointToStopPoint) &&
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
        return currentServicePointVersion == null ? true : !newServicePointVersion.getDesignationOfficial().equals(currentServicePointVersion.getDesignationOfficial());
    }

    private boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return currentServicePointVersion == null ? true : newServicePointVersion.isStopPoint() && !currentServicePointVersion.isStopPoint();
    }

    public Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
                                           Optional<ServicePointVersion> currentServicePointVersion,
                                           List<ServicePointVersion> servicePointVersions) {
        // case 1 new StopPoint, scenario 1
        if (currentServicePointVersion == null && (servicePointVersions == null || servicePointVersions.isEmpty())) {
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO1");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, null);
        }

        // case 2 change from servicePoint to stopPoint, scenario 2, scenario 3
        if (isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion.get())) {
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO2");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, currentServicePointVersion.get());
        }

        // case 3 change from stopPoint to stopPoint, with or without name change
        if (isNameChanged(newServicePointVersion, currentServicePointVersion.get())) {
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO3");
            // cover extensions of version with the same name, then status is validated
            if (isThereTouchingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) { // scenario 16, scenario 17, scenario 7, scenario 14, scenario 15, (14 and 15 depend on which version we do update, might not enter here)
                log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO3");
                log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO3 ISTHERETOUCHINGVERSIONWITHTHESAMENAME");
                return Status.VALIDATED;
            }

            Optional<ServicePointVersion> servicePointVersion = findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions);
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO3");
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO3 findPreviousVersionOnSameTimeslot" + servicePointVersion.isPresent());
            // scenario 4, scenario 5, scenario 6, scenario 8, scenario 9, scenario 10, scenario 11, scenario 12, scenario 13, scenario 18
            if (servicePointVersion.isPresent()) return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, currentServicePointVersion.get());
        }

        // case update with gap without name change, scenario 19
        if (checkIfVersionIsIsolated(newServicePointVersion, servicePointVersions)) {
            log.info("SCENARIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO with the gap");
            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, currentServicePointVersion.get());
        }
        log.info("SCENARIOOOOOOOO validated");
        return Status.VALIDATED;


//        if (servicePointVersions!=null && !servicePointVersions.isEmpty() && checkIfVersionIsIsolated(newServicePointVersion, servicePointVersions)) {
//            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, currentServicePointVersion.get());
////            return isStatusDraftAccordingToStatusDecisionAlgorithm(newServicePointVersion, currentServicePointVersion.get());
//        }
//
//        if (isStatusInReview(newServicePointVersion, currentServicePointVersion)) {
//            if (servicePointVersions == null || servicePointVersions.isEmpty()) {
//                return Status.DRAFT;
//            }
//            if (isThereTouchingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
//                return Status.VALIDATED;
//            }
//            Optional<ServicePointVersion> servicePointVersion = findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions);
//            if (servicePointVersion.isPresent()) return Status.DRAFT;
//        }
//        return Status.VALIDATED;
    }

    // for scenarios 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
    private Optional<ServicePointVersion> findPreviousVersionOnSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                            List<ServicePointVersion> currentServicePointVersions) {

        // this is for scenarios 12, 13, 18
        ServicePointVersion lastExistingServicePointVersion = getLastOfExistingVersions(currentServicePointVersions);
        ServicePointVersion firstExistingServicePointVersion = getFirstOfExistingVersions(currentServicePointVersions);
        if (lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom()) ||
                firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo())) {
            return currentServicePointVersions
                    .stream()
                    .filter(currentServicePointVersion ->
                        (isNameChanged(newServicePointVersion, currentServicePointVersion) ||
                        isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
                    .findFirst();
        }
        return currentServicePointVersions
                .stream()
                .filter(currentServicePointVersion ->
                        (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        && !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        && (isNameChanged(newServicePointVersion, currentServicePointVersion) || isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
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

//    private boolean isStatusInReview(ServicePointVersion newServicePointVersion,
//                                     Optional<ServicePointVersion> currentServicePointVersion) {
//        if (currentServicePointVersion == null) { // if it is stopPoint creation from scratch
//            return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, null);
//        }
//
////        if (newServicePointVersion.getId() == null) {
////            return checkStatus(newServicePointVersion);
////        }
//        else {
//            boolean isNameChanged = !newServicePointVersion.getDesignationOfficial()
//                    .equals(currentServicePointVersion.get().getDesignationOfficial());
//            if (isNameChanged) {
//                return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, null) && isNameChanged;
//            }
//            boolean isServicePointChange = !currentServicePointVersion.get().isStopPoint() && newServicePointVersion.isStopPoint();
//            if (isServicePointChange) {
//                return calculateStatusAccordingToStatusDecisionAlgorithm(newServicePointVersion, null);
//            }
//            else return false;
//        }
//    }





    /**
     * Pre-Save Versions: |------||------||------|
     *                               ^
     * Saving Version             |------|
     */
//    private Optional<ServicePointVersion> findPreviousVersionOnSameTimeslot(ServicePointVersion newServicePointVersion,
//                                                                            List<ServicePointVersion> currentServicePointVersions) {
//        ServicePointVersion lastExistingServicePointVersion = getLastOfExistingVersions(currentServicePointVersions);
//        ServicePointVersion firstExistingServicePointVersion = getFirstOfExistingVersions(currentServicePointVersions);
//        if (lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom()) ||
//                firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo())) {
//            return currentServicePointVersions.stream().filter(currentServicePointVersion ->
//                            (isNameChanged(newServicePointVersion, currentServicePointVersion) ||
//                                    isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
//                    .findFirst();
//        }
//        return currentServicePointVersions.stream().filter(currentServicePointVersion ->
//                        (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
//                                &&
//                                !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
//                                &&
//                                (isNameChanged(newServicePointVersion, currentServicePointVersion)
//                                        ||
//                                        isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
//                .findFirst();
//    }

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
        return existing.getValidFrom().isBefore(newOne.getValidTo()) && existing.getValidTo().isAfter(newOne.getValidFrom());
    }

}
