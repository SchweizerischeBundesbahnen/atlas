package ch.sbb.atlas.servicepointdirectory.module.servicepoint.helper;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.StopPointTerminationNotOnLastVersionException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationAlreadyInProgressException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationNotAllowedWhenVersionInWrongStatusException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationNotStopPointException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationStopPointCountyNotAllowedException;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTerminationHelper {

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
