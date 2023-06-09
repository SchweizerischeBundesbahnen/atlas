package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    if (permissionsForUserIdentifier.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(toModel(permissionsForUserIdentifier));
  }

  UserAdministrationModel toModel(List<Permission> permissions) {
    Set<UserAdministrationPermissionModel> permissionModels = permissions.stream()
        .map(permission -> UserAdministrationPermissionModel.builder()
            .application(permission.getApplication())
            .role(permission.getRole())
            .restrictions(permission.getPermissionRestrictions().stream()
                .map(restriction -> UserAdministrationPermissionRestrictionModel.builder()
                    .value(restriction.getRestriction())
                    .restrictionType(restriction.getType())
                    .build())
                .collect(Collectors.toSet()))
            .build())
        .collect(Collectors.toSet());
    return UserAdministrationModel.builder()
        .userId(permissions.get(0).getIdentifier())
        .permissions(permissionModels)
        .build();
  }

  public String getCurrentUserSbbUid() {
    return UserService.getUserIdentifier();
  }

  public boolean isAdmin() {
    return UserService.getRoles().contains("atlas-admin");
  }

}
