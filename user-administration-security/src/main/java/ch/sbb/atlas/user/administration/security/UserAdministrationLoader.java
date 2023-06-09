package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
@KafkaListener(topics = "${kafka.atlas.user.administration.topic}", groupId = "${kafka.atlas.user.administration.groupId}")
public class UserAdministrationLoader {

  private final PermissionRepository permissionRepository;

  @KafkaHandler
  public void readUserPermissionsFromKafka(UserAdministrationModel userAdministrationModel) {
    String userId = userAdministrationModel.getUserId();

    userAdministrationModel.getPermissions().forEach(permissionToUpdate -> {
      Optional<Permission> existingPermission = permissionRepository.findByIdentifierAndApplication(userId,
          permissionToUpdate.getApplication());

      if (existingPermission.isPresent()) {
        updateExistingPermission(permissionToUpdate, existingPermission.get());
      } else {
        addNewPermission(userId, permissionToUpdate);
      }
    });
  }

  private void addNewPermission(String userId, UserAdministrationPermissionModel permissionToUpdate) {
    Permission additionalPermission = Permission.builder()
        .identifier(userId)
        .role(permissionToUpdate.getRole())
        .application(permissionToUpdate.getApplication())
        .build();
    additionalPermission.setPermissionRestrictions(mapPermissionRestrictionsToEntity(permissionToUpdate, additionalPermission));
    permissionRepository.save(additionalPermission);
  }

  private static void updateExistingPermission(UserAdministrationPermissionModel permissionToUpdate,
      Permission updateableUserPermission) {
    updateableUserPermission.setRole(permissionToUpdate.getRole());

    updateableUserPermission.getPermissionRestrictions().clear();
    updateableUserPermission.getPermissionRestrictions().addAll(mapPermissionRestrictionsToEntity(permissionToUpdate, updateableUserPermission));
  }

  private static Set<PermissionRestriction> mapPermissionRestrictionsToEntity(
      UserAdministrationPermissionModel permissionToUpdate, Permission permission) {
    return permissionToUpdate.getRestrictions().stream().map(
            restriction -> PermissionRestriction.builder()
                .restriction(restriction.getValue())
                .type(restriction.getRestrictionType())
                .permission(permission)
                .build())
        .collect(Collectors.toSet());
  }
}
