package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServicePointTerminationBasedUserAdministrationService extends BaseUserAdministrationService {

  public ServicePointTerminationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean hasUserInfoPlusTerminationVotePermission() {
    log.debug("Checking if user {} has permission to vote for info plus", getCurrentUserSbbUid());

    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(ApplicationType.SEPODI);

    return userPermissionsForApplication.getRestrictions().stream()
        .anyMatch(
            r -> PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE.equals(r.getRestrictionType()) && Boolean.parseBoolean(
                r.getValue()));
  }

  public boolean hasUserNovaTerminationVotePermission() {
    log.debug("Checking if user {} has permission to vote for nova", getCurrentUserSbbUid());

    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(ApplicationType.SEPODI);

    return userPermissionsForApplication.getRestrictions().stream()
        .anyMatch(r -> PermissionRestrictionType.NOVA_TERMINATION_VOTE.equals(r.getRestrictionType()) && Boolean.parseBoolean(
            r.getValue()));
  }

}
