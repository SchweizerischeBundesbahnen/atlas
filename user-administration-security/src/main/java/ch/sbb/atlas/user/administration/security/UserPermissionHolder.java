package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.model.service.UserService;
import ch.sbb.atlas.user.administration.security.model.UserModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionHolder {

  private Map<String, UserModel> userPermissions = new HashMap<>();

  public UserModel getCurrentUser() {
    return userPermissions.get(UserService.getSbbUid());
  }

  public boolean isAdmin() {
    List<String> roles = UserService.getAccessToken().getClaim("roles");
    return roles.contains("atlas-admin");
  }

}
