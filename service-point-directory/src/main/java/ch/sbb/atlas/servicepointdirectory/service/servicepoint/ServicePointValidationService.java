package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointValidationService {

  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validateServicePointPreconditionBusinessRule(ServicePointVersion servicePointVersion) {
    sharedBusinessOrganisationService.validateSboidExists(servicePointVersion.getBusinessOrganisation());
  }
}
