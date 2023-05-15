package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CantonBasedUserAdministrationService extends BaseUserAdministrationService {

  public CantonBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean isAtLeastExplicitReader(ApplicationType applicationType) {
    return isAtLeast(ApplicationRole.EXPLICIT_READER, applicationType, null);
  }

  public boolean isAtLeastWriter(ApplicationType applicationType, CantonAssociated businessObject) {
    return isAtLeast(ApplicationRole.WRITER, applicationType, businessObject.getSwissCanton());
  }

  public boolean isAtLeastSupervisor(ApplicationType applicationType) {
    return isAtLeast(ApplicationRole.SUPERVISOR, applicationType, null);
  }

  private boolean isAtLeast(ApplicationRole applicationRole, ApplicationType applicationType, SwissCanton swissCanton) {
    log.info("Checking if user {} is at least {} for canton {}", getCurrentUserSbbUid(), applicationRole, swissCanton);
    if (isAdmin()) {
      log.info("User {} is admin", getCurrentUserSbbUid());
      return true;
    }
    if (applicationRole.equals(ApplicationRole.WRITER)) {
      log.info("User {}, with role WRITER is moving statement to another canton and this action is always allowed.", getCurrentUserSbbUid());
      return true;
    }
    ApplicationRole cantonBasedUserPermissions = getCantonBasedUserPermissions(applicationType, swissCanton);
    boolean hasRequiredRank = applicationRole.getRank() <= cantonBasedUserPermissions.getRank();
    log.info("User {} has permissions: {}", getCurrentUserSbbUid(), hasRequiredRank);
    return hasRequiredRank;
  }

  private ApplicationRole getCantonBasedUserPermissions(ApplicationType applicationType, SwissCanton swissCanton) {
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(applicationType);
    if (userPermissionsForApplication.getRole() == ApplicationRole.WRITER) {
      if (swissCanton != null && userPermissionsForApplication.getSwissCantons().contains(swissCanton)) {
        return ApplicationRole.WRITER;
      } else {
        return ApplicationRole.EXPLICIT_READER;
      }
    }
    return userPermissionsForApplication.getRole();
  }

}
