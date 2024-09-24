package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.abbreviationsallowlist.ServicePointAbbreviationAllowList;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.AbbreviationUpdateNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.ForbiddenDueToChosenServicePointVersionValidationPeriodException;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidAbbreviationException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationLongConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointDesignationOfficialConflictException;
import ch.sbb.atlas.servicepointdirectory.exception.UpdateAffectsInReviewVersionException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    if (servicePointVersion.getOperatingPointKilometerMaster() != null && !servicePointVersion.getOperatingPointKilometerMaster()
        .getNumber().equals(servicePointVersion.getNumber().getNumber())) {
      checkIfKilometerMasterNumberCanBeAssigned(servicePointVersion.getOperatingPointKilometerMaster(), servicePointVersion);
    }
    validateDesignationOfficialUniqueness(servicePointVersion);
    validateDesignationLongUniqueness(servicePointVersion);
    sharedBusinessOrganisationService.validateSboidExists(servicePointVersion.getBusinessOrganisation());
  }

  public void checkIfKilometerMasterNumberCanBeAssigned(ServicePointNumber kilometerMasterNumber,
      ServicePointVersion servicePointVersion) {
    List<ServicePointVersion> allKilometerMasterNumberVersions = servicePointVersionRepository
        .findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(
            kilometerMasterNumber);
    boolean result = new Timeline(allKilometerMasterNumberVersions,
        servicePointVersion).isSePoTimelineInsideOrEqToOneOfKilomMastTimelines();
    if (!result) {
      throw new ForbiddenDueToChosenServicePointVersionValidationPeriodException(kilometerMasterNumber);
    }
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

  public void validateAndSetAbbreviation(ServicePointVersion editedVersion) {
    boolean isBussinesOrganisationInList = ServicePointAbbreviationAllowList.SBOIDS.contains(
        editedVersion.getBusinessOrganisation());

    if ((isAbbreviationEqualWithPreviousVersions(editedVersion) || isServicePointNew(editedVersion))) {
      return;
    }

    if (!isBussinesOrganisationInList || hasServicePointVersionsAbbreviation(editedVersion)) {
      throw new AbbreviationUpdateNotAllowedException();
    }

    if (isServicePointHighDateVersion(editedVersion) || !isAbbreviationUnique(editedVersion)) {
      throw new InvalidAbbreviationException();
    }
  }

  private boolean isAbbreviationUnique(ServicePointVersion servicePointVersion) {
    return servicePointVersionRepository.findServicePointVersionByAbbreviation(servicePointVersion.getAbbreviation())
        .stream()
        .noneMatch(obj -> !obj.getNumber().equals(servicePointVersion.getNumber()));
  }

  private boolean isServicePointHighDateVersion(ServicePointVersion servicePointVersion) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(servicePointVersion.getNumber())
        .stream()
        .anyMatch(obj -> obj.getValidTo().isAfter(servicePointVersion.getValidTo()));
  }

  private boolean isAbbreviationEqualWithPreviousVersions(ServicePointVersion editedVersion) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(editedVersion.getNumber())
        .stream()
        .anyMatch(obj -> Objects.equals(obj.getAbbreviation(), editedVersion.getAbbreviation()));
  }

  private boolean isServicePointNew(ServicePointVersion editedVersion) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(editedVersion.getNumber()).isEmpty()
        && StringUtils.isBlank(editedVersion.getAbbreviation());
  }

  private boolean hasServicePointVersionsAbbreviation(ServicePointVersion editedVersion) {
    return servicePointVersionRepository.findAllByNumberOrderByValidFrom(editedVersion.getNumber())
        .stream()
        .anyMatch(obj -> StringUtils.isNotBlank(obj.getAbbreviation()) && !Objects.equals(obj.getAbbreviation(),
            editedVersion.getAbbreviation()));
  }

  public void checkNotAffectingInReviewVersions(List<ServicePointVersion> existingVersions,      ServicePointVersion editedVersion) {
    List<ServicePointVersion> affectedVersions =
        existingVersions.stream()
            .filter(version -> version.getStatus() == Status.IN_REVIEW && new AffectingVersionValidator(editedVersion,
                version).check())
            .toList();

    if (!affectedVersions.isEmpty()) {
      throw new UpdateAffectsInReviewVersionException(
          editedVersion.getValidFrom(),
          editedVersion.getValidTo(),
          affectedVersions
      );
    }
  }

  public List<ServicePointVersion> validateNoMergeAffectVersionInReview(ServicePointVersion currentVersion,
      List<ServicePointVersion> existingDbVersionInReview) {
    List<ServicePointVersion> afterUpdateServicePoint = servicePointVersionRepository.findAllByNumberOrderByValidFrom(
        currentVersion.getNumber());

    List<ServicePointVersion> afterUpdateServicePointInReview = servicePointVersionRepository.findAllByNumberOrderByValidFrom(
            currentVersion.getNumber()).stream()
        .filter(servicePointVersion -> Status.IN_REVIEW == servicePointVersion.getStatus()).toList();
    
    if (!existingDbVersionInReview.isEmpty()) {
      if (existingDbVersionInReview.size() != afterUpdateServicePointInReview.size()) {
        throw new UpdateAffectsInReviewVersionException(
            existingDbVersionInReview.getFirst().getValidFrom(),
            existingDbVersionInReview.getLast().getValidTo(),
            existingDbVersionInReview);
      }
      for (int i = 0; i < existingDbVersionInReview.size(); i++) {
        ServicePointVersion existingVersion = existingDbVersionInReview.get(i);
        ServicePointVersion afterVersion = afterUpdateServicePointInReview.get(i);
        if (!existingVersion.getValidFrom().equals(afterVersion.getValidFrom()) ||
            !existingVersion.getValidTo().equals(afterVersion.getValidTo())) {
          throw new UpdateAffectsInReviewVersionException(
              afterUpdateServicePointInReview.getFirst().getValidFrom(),
              afterUpdateServicePointInReview.getLast().getValidTo(),
              afterUpdateServicePointInReview);
        }
      }
    }
    return afterUpdateServicePoint;
  }

  @RequiredArgsConstructor
  private static final class AffectingVersionValidator {

    private final Versionable updateVersionModel;
    private final ServicePointVersion version;

    private boolean check() {
      return isValidFromInsideUpdate()
          || isValidToInsideUpdate()
          || isUpdateInsideVersion();
    }

    private boolean isValidFromInsideUpdate() {
      return isAfterOrEqual(version.getValidFrom(), updateVersionModel.getValidFrom())
          && isBeforeOrEqual(version.getValidFrom(), updateVersionModel.getValidTo());
    }

    private boolean isValidToInsideUpdate() {
      return isAfterOrEqual(version.getValidTo(), updateVersionModel.getValidFrom())
          && isBeforeOrEqual(version.getValidTo(), updateVersionModel.getValidTo());
    }

    private boolean isUpdateInsideVersion() {
      return isAfterOrEqual(version.getValidTo(), updateVersionModel.getValidTo())
          && isBeforeOrEqual(version.getValidFrom(), updateVersionModel.getValidFrom());
    }

    private boolean isAfterOrEqual(LocalDate date, LocalDate comparedDate) {
      return date.isAfter(comparedDate) || date.isEqual(comparedDate);
    }

    private boolean isBeforeOrEqual(LocalDate date, LocalDate comparedDate) {
      return date.isBefore(comparedDate) || date.isEqual(comparedDate);
    }

  }

}
