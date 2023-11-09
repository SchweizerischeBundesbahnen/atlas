package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseUserAdministrationService {

  private final UserPermissionHolder userPermissionHolder;

  protected String getCurrentUserSbbUid() {
    return userPermissionHolder.getCurrentUserSbbUid();
  }

  protected boolean isAdmin() {
    return userPermissionHolder.isAdmin();
  }

  protected boolean hasUserPermissions(ApplicationType applicationType,
      Predicate<UserAdministrationPermissionModel> writerPermissionCheck) {
    if (isAdmin()) {
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

  protected UserAdministrationPermissionModel getUserPermissionsForApplication(
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
    if (userPermissionsForCurrentApplication.isEmpty()) {
      return UserAdministrationPermissionModel.builder().application(applicationType).role(ApplicationRole.READER).build();
    }
    throw new IllegalStateException(
        "Found multiple Permissions for application " + applicationType + " and user "
            + getCurrentUserSbbUid());
  }

}
