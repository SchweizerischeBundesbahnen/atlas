package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.user.administration.security.model.ApplicationRole;
import ch.sbb.atlas.user.administration.security.model.ApplicationType;
import ch.sbb.atlas.user.administration.security.model.UserPermissionModel;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionHolder userPermissionHolder;

  public boolean hasUserPermissionsToCreate(BusinessOrganisationAssociated businessObject,
      ApplicationType applicationType) {
    if (userPermissionHolder.isAdmin()) {
      return true;
    }
    UserPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    if (roleForApplication == ApplicationRole.SUPERVISOR ||
        roleForApplication == ApplicationRole.SUPER_USER) {
      return true;
    }

    if (roleForApplication == ApplicationRole.WRITER) {
      return userPermissionsForApplication.getSboids()
                                          .contains(businessObject.getBusinessOrganisation());
    }
    return false;
  }

  public boolean hasUserPermissionsToUpdate(BusinessOrganisationAssociated editedBusinessObject,
      List<BusinessOrganisationAssociated> currentBusinessObjects,
      ApplicationType applicationType) {
    if (userPermissionHolder.isAdmin()) {
      return true;
    }
    UserPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    if (roleForApplication == ApplicationRole.SUPERVISOR ||
        roleForApplication == ApplicationRole.SUPER_USER) {
      return true;
    }

    if (roleForApplication == ApplicationRole.WRITER) {
      return userPermissionsForApplication.getSboids()
                                          .containsAll(findUpdateAffectedCurrentVersions(
                                              editedBusinessObject, currentBusinessObjects).stream()
                                                                                           .map(
                                                                                               BusinessOrganisationAssociated::getBusinessOrganisation)
                                                                                           .toList());
    }
    return false;
  }

  List<BusinessOrganisationAssociated> findUpdateAffectedCurrentVersions(
      BusinessOrganisationAssociated editedBusinessObject,
      List<BusinessOrganisationAssociated> currentBusinessObjects) {
    LocalDate validFrom = editedBusinessObject.getValidFrom();
    LocalDate validTo = editedBusinessObject.getValidTo();

    return currentBusinessObjects.stream()
                                 .filter(currentVersion ->
                                     !currentVersion.getValidTo().isBefore(validFrom) &&
                                         !currentVersion.getValidFrom().isAfter(validTo))
                                 .toList();
  }

  private UserPermissionModel getUserPermissionsForApplication(ApplicationType applicationType) {
    List<UserPermissionModel> userPermissionsForCurrentApplication = userPermissionHolder.getCurrentUser()
                                                                                         .getPermissions()
                                                                                         .stream()
                                                                                         .filter(
                                                                                             i -> i.getApplication()
                                                                                                   .equals(
                                                                                                       applicationType))
                                                                                         .toList();
    if (userPermissionsForCurrentApplication.size() == 1) {
      return userPermissionsForCurrentApplication.get(0);
    }
    throw new IllegalStateException(
        "Found multiple Permissions for application " + applicationType + " and user "
            + userPermissionHolder.getCurrentUser().getSbbUserId());
  }

}
