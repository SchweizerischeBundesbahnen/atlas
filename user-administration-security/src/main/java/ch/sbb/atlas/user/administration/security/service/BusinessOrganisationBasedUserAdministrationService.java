package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.atlas.redact.RedactBySboidDecider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@Primary
public class BusinessOrganisationBasedUserAdministrationService extends BaseUserAdministrationService implements
    RedactBySboidDecider {

  private static final Predicate<UserAdministrationPermissionRestrictionModel> IS_BUSINESS_ORGANISATION_RESTRICTION =
          i -> i.getRestrictionType() == PermissionRestrictionType.BUSINESS_ORGANISATION;

  public BusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean hasUserPermissionsToCreate(BusinessOrganisationAssociated businessObject,
                                            ApplicationType applicationType) {
    return hasUserPermissionsForBusinessOrganisation(businessObject.getBusinessOrganisation(), applicationType);
  }

  @Override
  public boolean hasUserPermissionsForBusinessOrganisation(String sboid,
      ApplicationType applicationType) {
    log.debug("Checking if user {} has enough rights for an object with sboid {}",
        getCurrentUserSbbUid(),
        sboid);

    boolean permissionsToCreate = hasUserPermissions(applicationType,
            permissions -> permissions.getRestrictions().stream()
                    .filter(IS_BUSINESS_ORGANISATION_RESTRICTION)
                    .map(UserAdministrationPermissionRestrictionModel::getValue).collect(Collectors.toSet())
                    .contains(sboid));

    log.debug("User {} has permissions: {}", getCurrentUserSbbUid(), permissionsToCreate);
    return permissionsToCreate;
  }

  public boolean hasUserPermissionsToUpdate(BusinessOrganisationAssociated editedBusinessObject,
      List<BusinessOrganisationAssociated> currentBusinessObjects,
      ApplicationType applicationType) {
    log.debug("Checking if user {} may update object {}", getCurrentUserSbbUid(), editedBusinessObject);

    boolean permissionsToUpdate = hasUserPermissions(applicationType,
        permissions -> permissions.getRestrictions().stream()
            .filter(IS_BUSINESS_ORGANISATION_RESTRICTION)
            .map(UserAdministrationPermissionRestrictionModel::getValue).collect(Collectors.toSet())
            .containsAll(UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
                    editedBusinessObject,
                    currentBusinessObjects).stream()
                .map(BusinessOrganisationAssociated::getBusinessOrganisation)
                .toList()));
    log.debug("User {} has permissions: {}", getCurrentUserSbbUid(), permissionsToUpdate);

    return permissionsToUpdate;
  }

  public boolean isAtLeastSupervisor(ApplicationType applicationType) {
    log.debug("Checking if user {} is at least supervisor for {}", getCurrentUserSbbUid(), applicationType);

    if (isAdmin()) {
      return true;
    }
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    return ApplicationRole.SUPERVISOR == roleForApplication;
  }

}
