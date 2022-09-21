package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
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
    log.info("Checking if user {} may create object with sboid {}",
        userPermissionHolder.getCurrentUserSbbUid(),
        businessObject.getBusinessOrganisation());
    boolean permissionsToCreate = hasUserPermissions(applicationType,
        permissions -> permissions.getSboids()
                                  .contains(
                                      businessObject.getBusinessOrganisation()));
    log.info("User {} has permissions: {}", userPermissionHolder.getCurrentUserSbbUid(),
        permissionsToCreate);
    return permissionsToCreate;

  }

  public boolean hasUserPermissionsToUpdate(BusinessOrganisationAssociated editedBusinessObject,
      List<BusinessOrganisationAssociated> currentBusinessObjects,
      ApplicationType applicationType) {
    log.info("Checking if user {} may update object {}",
        userPermissionHolder.getCurrentUserSbbUid(),
        editedBusinessObject);
    boolean permissionsToUpdate = hasUserPermissions(applicationType,
        permissions -> permissions.getSboids()
                                  .containsAll(
                                      findUpdateAffectedCurrentVersions(
                                          editedBusinessObject,
                                          currentBusinessObjects).stream()
                                                                 .map(
                                                                     BusinessOrganisationAssociated::getBusinessOrganisation)
                                                                 .toList()));
    log.info("User {} has permissions: {}", userPermissionHolder.getCurrentUserSbbUid(),
        permissionsToUpdate);
    return permissionsToUpdate;
  }

  private boolean hasUserPermissions(ApplicationType applicationType,
      Predicate<UserAdministrationPermissionModel> writerPermissionCheck) {
    if (userPermissionHolder.isAdmin()) {
      return true;
    }
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    if (Set.of(ApplicationRole.SUPERVISOR, ApplicationRole.SUPER_USER)
           .contains(roleForApplication)) {
      return true;
    }

    if (roleForApplication == ApplicationRole.WRITER) {
      return writerPermissionCheck.test(userPermissionsForApplication);
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

  private UserAdministrationPermissionModel getUserPermissionsForApplication(
      ApplicationType applicationType) {
    Optional<UserAdministrationModel> currentUserPermissions = userPermissionHolder.getCurrentUser();
    if (currentUserPermissions.isEmpty()) {
      return UserAdministrationPermissionModel.builder().application(applicationType).role(ApplicationRole.READER).build();
    }
    List<UserAdministrationPermissionModel> userPermissionsForCurrentApplication = currentUserPermissions.orElseThrow()
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
            + userPermissionHolder.getCurrentUserSbbUid());
  }

  public boolean isAtLeastSupervisor(ApplicationType applicationType) {
    log.info("Checking if user {} is at least supervisor for {}",
            userPermissionHolder.getCurrentUserSbbUid(),
            applicationType);
    if (userPermissionHolder.isAdmin()) {
      return true;
    }
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
            applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    return ApplicationRole.SUPERVISOR == roleForApplication;
  }

}
