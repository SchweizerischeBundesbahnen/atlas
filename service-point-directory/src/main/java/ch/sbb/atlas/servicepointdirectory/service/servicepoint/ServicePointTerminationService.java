package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedException;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointTerminationService {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  public void checkTerminationAllowed(List<ServicePointVersion> currentVersions, List<ServicePointVersion> afterUpdate) {
    boolean isTermination = isTermination(currentVersions, afterUpdate);
    ServicePointNumber number = currentVersions.iterator().next().getNumber();
    log.info("Update on {}. isTermination={}", number, isTermination);

    if (isTermination && !businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)) {
      throw new TerminationNotAllowedException(number);
    }
  }

  private static boolean isTermination(List<ServicePointVersion> currentVersions, List<ServicePointVersion> afterUpdateVersions) {
    Validity preUpdateStopPointValidity = getValidityOfStopPoint(currentVersions);
    Validity afterUpdateStopPointValidity = getValidityOfStopPoint(afterUpdateVersions);
    return !afterUpdateStopPointValidity.containsEveryDateOf(preUpdateStopPointValidity);
  }

  private static Validity getValidityOfStopPoint(List<ServicePointVersion> servicePoint) {
    return new Validity(
        servicePoint.stream().filter(ServicePointVersion::isStopPoint).map(DateRange::fromVersionable).toList()).minify();
  }

}
