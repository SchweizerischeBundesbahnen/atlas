package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class ServicePointStatusDecider {

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

    private boolean isStatusInReview(ServicePointVersion newServicePointVersion,
                                     Optional<ServicePointVersion> currentServicePointVersion) {
        if (currentServicePointVersion == null) { // if it is stopPoint creation from scratch
            return checkStatus(newServicePointVersion);
        }

//        if (newServicePointVersion.getId() == null) {
//            return checkStatus(newServicePointVersion);
//        }
        else {
            boolean isNameChanged = !newServicePointVersion.getDesignationOfficial()
                    .equals(currentServicePointVersion.get().getDesignationOfficial());
            if (isNameChanged) {
                return checkStatus(newServicePointVersion) && isNameChanged;
            }
            boolean isServicePointChange = !currentServicePointVersion.get().isStopPoint() && newServicePointVersion.isStopPoint();
            if (isServicePointChange) {
                return checkStatus(newServicePointVersion);
            }
            else return false;
        }
    }

    private boolean isNameChanged(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return !newServicePointVersion.getDesignationOfficial().equals(currentServicePointVersion.getDesignationOfficial());
    }

    private boolean isChangeFromServicePointToStopPoint(ServicePointVersion newServicePointVersion, ServicePointVersion currentServicePointVersion) {
        return newServicePointVersion.isStopPoint() && !currentServicePointVersion.isStopPoint();
    }

    private boolean checkStatus(ServicePointVersion newServicePointVersion) {
        boolean isSwissCountryCode = Objects.equals(newServicePointVersion.getCountry().getUicCode(), Country.SWITZERLAND.getUicCode());
        boolean isValidityLongEnough = ChronoUnit.DAYS.between(newServicePointVersion.getValidFrom(), newServicePointVersion.getValidTo()) > 60;

        return isSwissCountryCode &&
                newServicePointVersion.isStopPoint() &&
                isValidityLongEnough;
    }

    public Status getStatusForServicePoint(ServicePointVersion newServicePointVersion,
                                           Optional<ServicePointVersion> currentServicePointVersion,
                                           List<ServicePointVersion> servicePointVersions) {
        if (servicePointVersions!=null && !servicePointVersions.isEmpty() && checkIfVersionIsIsolated(newServicePointVersion, servicePointVersions) && newServicePointVersion.isStopPoint()) {
            return Status.DRAFT;
        }

        if (isStatusInReview(newServicePointVersion, currentServicePointVersion)) {
            if (servicePointVersions == null || servicePointVersions.isEmpty()) {
                return Status.DRAFT;
            }
            if (isThereTouchingVersionWithTheSameName(newServicePointVersion, servicePointVersions)) {
                return Status.VALIDATED;
            }
            Optional<ServicePointVersion> servicePointVersion = findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions);
            if (servicePointVersion.isPresent()) return Status.DRAFT;
        }
        return Status.VALIDATED;
    }

    /**
     * Pre-Save Versions: |------||------||------|
     *                               ^
     * Saving Version             |------|
     */
    private Optional<ServicePointVersion> findPreviousVersionOnSameTimeslot(ServicePointVersion newServicePointVersion,
                                                                            List<ServicePointVersion> currentServicePointVersions) {
        ServicePointVersion lastExistingServicePointVersion = getLastOfExistingVersions(currentServicePointVersions);
        ServicePointVersion firstExistingServicePointVersion = getFirstOfExistingVersions(currentServicePointVersions);
        if (lastExistingServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom()) ||
                firstExistingServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidTo())) {
            return currentServicePointVersions.stream().filter(currentServicePointVersion ->
                        (isNameChanged(newServicePointVersion, currentServicePointVersion) ||
                        isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
                    .findFirst();
        }
        return currentServicePointVersions.stream().filter(currentServicePointVersion ->
                        (!currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom())
                        &&
                        !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom()))
                        &&
                        (isNameChanged(newServicePointVersion, currentServicePointVersion)
                        ||
                        isChangeFromServicePointToStopPoint(newServicePointVersion, currentServicePointVersion)))
                .findFirst();
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
        return existing.getValidFrom().isBefore(newOne.getValidTo()) && existing.getValidTo().isAfter(newOne.getValidFrom());
    }

}
