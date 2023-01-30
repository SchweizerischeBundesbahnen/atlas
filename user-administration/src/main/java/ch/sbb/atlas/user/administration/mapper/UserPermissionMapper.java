package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionVersionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionMapper {

  public static UserPermissionVersionModel toModel(UserPermission userPermission) {
    return UserPermissionVersionModel.builder()
        .role(userPermission.getRole())
        .application(userPermission.getApplication())
        .sboids(userPermission.getSboid().stream().toList())
        .editor(userPermission.getEditor())
        .editionDate(userPermission.getEditionDate())
        .creator(userPermission.getCreator())
        .creationDate(userPermission.getCreationDate())
        .build();
  }

}
