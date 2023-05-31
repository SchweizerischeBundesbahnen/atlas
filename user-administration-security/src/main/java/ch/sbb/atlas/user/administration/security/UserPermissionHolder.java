package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserPermissionHolder {

  private final PermissionRepository permissionRepository;

  public Optional<UserAdministrationModel> getCurrentUser() {
    List<Permission> permissionsForUserIdentifier = permissionRepository.findAllByIdentifier(UserService.getUserIdentifier());
    return Optional.ofNullable(userPermissions.get());
  }

  public String getCurrentUserSbbUid() {
    return UserService.getUserIdentifier();
  }

  public boolean isAdmin() {
    return UserService.getRoles().contains("atlas-admin");
  }

}
