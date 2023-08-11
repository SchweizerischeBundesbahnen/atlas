package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationLongConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationOfficialConflictException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointValidationService {

  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  public void validateServicePointPreconditionBusinessRule(ServicePointVersion servicePointVersion) {
    validateDesignationOfficialUniqueness(servicePointVersion);
    validateDesignationLongUniqueness(servicePointVersion);
    sharedBusinessOrganisationService.validateSboidExists(servicePointVersion.getBusinessOrganisation());
  }

  private void validateDesignationOfficialUniqueness(ServicePointVersion servicePointVersion) {
    List<ServicePointVersion> designationOfficialOverlaps = servicePointVersionRepository.findDesignationOfficialOverlaps(
        servicePointVersion);
    if (!designationOfficialOverlaps.isEmpty()) {
      throw new ServicePointDesignationOfficialConflictException(servicePointVersion, designationOfficialOverlaps);
    }
  }

  private void validateDesignationLongUniqueness(ServicePointVersion servicePointVersion) {
    if (servicePointVersion.getDesignationLong() != null) {
      List<ServicePointVersion> designationLongOverlaps = servicePointVersionRepository.findDesignationLongOverlaps(
          servicePointVersion);
      if (!designationLongOverlaps.isEmpty()) {
        throw new ServicePointDesignationLongConflictException(servicePointVersion, designationLongOverlaps);
      }
    }
  }
}
