package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.model.service.UserService;
import ch.sbb.atlas.user.administration.security.model.UserModel;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionHolder {

  private Map<String, UserModel> userPermissions = new HashMap<>();

  public UserModel getCurrentUser() {
    return userPermissions.get(UserService.getSbbUid());
  }

  public String getCurrentUserSbbUid() {
    return UserService.getSbbUid();
  }

  public boolean isAdmin() {
    return UserService.getRoles().contains("ROLE_atlas-admin");
  }

}
