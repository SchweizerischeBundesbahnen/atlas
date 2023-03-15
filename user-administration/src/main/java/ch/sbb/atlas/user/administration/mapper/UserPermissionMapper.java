package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.HashSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionMapper {

  public static UserPermissionModel toModel(UserPermission userPermission) {
    return UserPermissionModel.builder()
        .role(userPermission.getRole())
        .application(userPermission.getApplication())
        .sboids(userPermission.getSboid().stream().toList())
        .swissCantons(userPermission.getSwissCantons().stream().toList())
        .editor(userPermission.getEditor())
        .editionDate(userPermission.getEditionDate())
        .creator(userPermission.getCreator())
        .creationDate(userPermission.getCreationDate())
        .build();
  }

  public static UserPermission toEntity(String sbbUserId, UserPermissionModel userPermissionModel) {
    return UserPermission.builder()
        .sbbUserId(sbbUserId)
        .role(userPermissionModel.getRole())
        .application(userPermissionModel.getApplication())
        .sboid(new HashSet<>(userPermissionModel.getSboids()))
        .swissCantons(new HashSet<>(userPermissionModel.getSwissCantons()))
        .build();
  }

}
