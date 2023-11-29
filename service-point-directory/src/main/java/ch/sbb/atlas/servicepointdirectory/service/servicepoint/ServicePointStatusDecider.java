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

    private boolean isStatusInReview(ServicePointVersion newServicePointVersion,
                                     Optional<ServicePointVersion> currentServicePointVersion) {
        if (currentServicePointVersion == null) { // if it is new version creation
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
        if (isStatusInReview(newServicePointVersion, currentServicePointVersion)) {
            if (servicePointVersions == null || servicePointVersions.isEmpty()) {
                return Status.IN_REVIEW;
            }
            Optional<ServicePointVersion> servicePointVersion = findPreviousVersionOnSameTimeslot(newServicePointVersion, servicePointVersions);
            if (servicePointVersion.isPresent()) return Status.IN_REVIEW;
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
        return currentServicePointVersions.stream().filter(currentServicePointVersion ->
                !currentServicePointVersion.getValidTo().isBefore(newServicePointVersion.getValidFrom()) &&
                        !currentServicePointVersion.getValidFrom().isAfter(newServicePointVersion.getValidFrom())).findFirst();
    }

}
