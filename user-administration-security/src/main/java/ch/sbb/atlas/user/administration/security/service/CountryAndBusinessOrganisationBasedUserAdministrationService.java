package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.api.model.CountryAndBusinessOrganisationAssociated;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CountryAndBusinessOrganisationBasedUserAdministrationService extends
    BusinessOrganisationBasedUserAdministrationService {

  public CountryAndBusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean hasUserPermissionsToCreateOrEditServicePointDependentObject(List<CountryAndBusinessOrganisationAssociated> servicePointVersions,
      ApplicationType applicationType) {
    if (servicePointVersions == null || servicePointVersions.isEmpty()) {
      log.error("List of ServicePointVersions was empty. Cannot perform check permissions. Will deny operation");
      return false;
    }
    log.debug("Checking if user may create or edit any of given list");
    return servicePointVersions.stream().anyMatch(obj -> hasUserPermissionsToCreate(obj, applicationType));
  }

  public boolean hasUserPermissionsToCreate(CountryAndBusinessOrganisationAssociated businessObject,
      ApplicationType applicationType) {
    log.debug("Checking if user {} may create object with country {} and sboid {}",
        getCurrentUserSbbUid(), businessObject.getCountry(), businessObject.getBusinessOrganisation());

    boolean hasPermissionsOnCountry = hasPermissionsForCountry(applicationType,
        Collections.singleton(businessObject.getCountry()));
    boolean hasPermissionsOnBusinessOrganisation = super.hasUserPermissionsToCreate(businessObject, applicationType);

    boolean permissionsToCreate = hasPermissionsOnCountry && hasPermissionsOnBusinessOrganisation;

    log.debug("User {} has permissions: {}", getCurrentUserSbbUid(), permissionsToCreate);
    return permissionsToCreate;
  }

  public boolean hasUserPermissionsToUpdateCountryBased(CountryAndBusinessOrganisationAssociated editedBusinessObject,
      List<CountryAndBusinessOrganisationAssociated> currentBusinessObjects, ApplicationType applicationType) {
    log.debug("Checking if user {} may update object {}", getCurrentUserSbbUid(), editedBusinessObject);

    boolean permissionsToUpdateBasedOnBusinessOrganisation = super.hasUserPermissionsToUpdate(editedBusinessObject,
        currentBusinessObjects.stream().map(i -> (BusinessOrganisationAssociated) i).toList(), applicationType);

    Set<Country> updateAffectedCountries = UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
            editedBusinessObject,
            currentBusinessObjects).stream()
        .map(CountryAndBusinessOrganisationAssociated::getCountry)
        .collect(Collectors.toSet());
    boolean permissionsToUpdateBasedOnCountry = hasPermissionsForCountry(applicationType, updateAffectedCountries);

    boolean permissionsToUpdate = permissionsToUpdateBasedOnBusinessOrganisation && permissionsToUpdateBasedOnCountry;

    log.debug("User {} has permissions: {}", getCurrentUserSbbUid(), permissionsToUpdate);
    return permissionsToUpdate;
  }

  private boolean hasPermissionsForCountry(ApplicationType applicationType, Set<Country> countries) {
    if (isAdmin()) {
      return true;
    }
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    if (roleForApplication == ApplicationRole.SUPERVISOR) {
      return true;
    }

    Set<Country> allowedCountries = userPermissionsForApplication.getRestrictions().stream()
        .filter(i -> i.getRestrictionType() == PermissionRestrictionType.COUNTRY)
        .map(UserAdministrationPermissionRestrictionModel::getValue)
        .map(Country::valueOf)
        .collect(Collectors.toSet());
    return allowedCountries.containsAll(countries);
  }
}
