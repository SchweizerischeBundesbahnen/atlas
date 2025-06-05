package ch.sbb.atlas.servicepointdirectory.helper;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.StopPointTerminationNotOnLastVersionException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationAlreadyInProgressException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedValidToNotWithinLastVersionRangeException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedWhenVersionInWrongStatusException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotStopPointException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationStopPointCountyNotAllowedException;
import java.time.LocalDate;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationHelper {

  public static void isValidToInLastVersionRange(String sloid, DateRange dateRange, LocalDate validTo) {
    if (!dateRange.contains(validTo)) {
      throw new TerminationNotAllowedValidToNotWithinLastVersionRangeException(sloid, validTo, dateRange.getFrom(),
          dateRange.getTo());
    }
  }

  public static ServicePointVersion checkIsStopPointTerminationWorkflowAllowed(String sloid, Long id,
      List<ServicePointVersion> servicePointVersions) {
    if (servicePointVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    ServicePointVersion servicePointVersion = servicePointVersions.stream().filter(sp -> sp.getId().equals(id)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(id));
    if (!servicePointVersions.getLast().getId().equals(id)) {
      throw new StopPointTerminationNotOnLastVersionException();
    }
    if (servicePointVersion.getStatus() != Status.VALIDATED) {
      throw new TerminationNotAllowedWhenVersionInWrongStatusException(servicePointVersion.getNumber(),
          servicePointVersion.getStatus());
    }
    if (!servicePointVersion.isStopPoint()) {
      throw new TerminationNotStopPointException();
    }
    if (!Country.TERMINABLE_WORKFLOW_COUNTRIES.contains(servicePointVersion.getCountry())) {
      throw new TerminationStopPointCountyNotAllowedException(servicePointVersion.getCountry());
    }

    servicePointVersions.stream().filter(ServicePointVersion::isTerminationInProgress).findAny().ifPresent(sp -> {
      throw new TerminationAlreadyInProgressException();
    });
    return servicePointVersion;
  }

}
