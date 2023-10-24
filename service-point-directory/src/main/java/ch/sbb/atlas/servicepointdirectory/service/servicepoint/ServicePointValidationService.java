package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.abbreviationsallowlist.ServicePointAbbreviationAllowList;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.AbbreviationUpdateNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidAbbreviationException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationLongConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationOfficialConflictException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  public void validateAndSetAbbreviationForCreate(ServicePointVersion servicePointVersion) {
    if (StringUtils.isBlank(servicePointVersion.getAbbreviation())) {
      return;
    }
    commonAbbreviationValidation(servicePointVersion);
  }

  public void validateAndSetAbbreviationForUpdate(ServicePointVersion existingServicePointVersion, ServicePointVersion editedVersion) {
    if (StringUtils.isBlank(editedVersion.getAbbreviation()) && StringUtils.isBlank(existingServicePointVersion.getAbbreviation())) {
      return;
    }

    if(hasServicePointVersionAbbreviation(existingServicePointVersion, editedVersion)){
      throw new AbbreviationUpdateNotAllowedException();
    }

    if(isHighDateVersion(editedVersion)) {
      throw new InvalidAbbreviationException();
    }

    commonAbbreviationValidation(editedVersion);
  }

  public void commonAbbreviationValidation(ServicePointVersion servicePointVersion) {
    boolean isBussinesOrganisationInList = ServicePointAbbreviationAllowList.SBOIDS.contains(servicePointVersion.getBusinessOrganisation());
    if(!isBussinesOrganisationInList) {
      throw new AbbreviationUpdateNotAllowedException();
    }

    if(!isAbbrevitionUnique(servicePointVersion)) {
      throw new InvalidAbbreviationException();
    }
  }

  private boolean isAbbrevitionUnique(ServicePointVersion servicePointVersion){
    return servicePointVersionRepository.findServicePointVersionByAbbreviation(servicePointVersion.getAbbreviation())
        .stream()
        .noneMatch(obj -> !obj.getNumber().equals(servicePointVersion.getNumber()));
  }

  private boolean isHighDateVersion(ServicePointVersion servicePointVersion){
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber())
        .stream()
        .anyMatch(obj -> obj.getValidTo().isAfter(servicePointVersion.getValidTo()));
  }

  private boolean hasServicePointVersionAbbreviation(ServicePointVersion servicePointVersion, ServicePointVersion editedVersion){
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber())
        .stream()
        .anyMatch(obj -> StringUtils.isNotBlank(obj.getAbbreviation()) && !obj.getAbbreviation().equals(editedVersion.getAbbreviation()));
  }
}
