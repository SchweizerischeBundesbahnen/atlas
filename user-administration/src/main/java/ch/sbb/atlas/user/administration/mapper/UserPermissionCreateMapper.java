package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserPermissionCreateMapper {

  public static List<UserPermission> toEntityList(UserPermissionCreateModel userPermissionCreateModel) {
    Stream<UserPermission> userPermissions = Arrays.stream(ApplicationType.values()).map(application ->
        UserPermission.builder()
            .sbbUserId(userPermissionCreateModel.getSbbUserId().toLowerCase())
            .application(application)
            .role(ApplicationRole.READER)
            .build()
    );
    return userPermissions.toList();
  }
}
