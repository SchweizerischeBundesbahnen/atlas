package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserPermissionHolder {

  private final Map<String, UserAdministrationModel> userPermissions = new HashMap<>();

  public Optional<UserAdministrationModel> getCurrentUser() {
    return Optional.ofNullable(userPermissions.get(UserService.getUserIdentifier()));
  }

  public void putUserPermissions(String sbbuid, UserAdministrationModel userAdministrationModel) {
    log.info("Adding {} with model {} to userpermissions", sbbuid, userAdministrationModel);
    userPermissions.put(sbbuid, userAdministrationModel);
  }

  public String getCurrentUserSbbUid() {
    return UserService.getUserIdentifier();
  }

  public boolean isAdmin() {
    return UserService.getRoles().contains("atlas-admin");
  }

}
