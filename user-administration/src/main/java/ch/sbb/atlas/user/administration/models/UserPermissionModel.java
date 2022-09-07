package ch.sbb.atlas.user.administration.models;

import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserPermissionModel(Long id, ApplicationRole role, ApplicationType application,
                                  Set<String> sboids) {

  public static UserPermissionModel toModel(UserPermission userPermission) {
    return UserPermissionModel.builder()
                              .id(userPermission.getId())
                              .role(userPermission.getRole())
                              .application(userPermission.getApplication())
                              .sboids(userPermission.getSboid())
                              .build();
  }

}
