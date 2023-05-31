package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.UserAdministrationEvent;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaModelMapper {

  public static UserAdministrationModel toKafkaModel(UserAdministrationEvent userModel) {
    Set<UserAdministrationPermissionModel> permissionModels = userModel.getPermissions().stream()
        .map(
            permission -> UserAdministrationPermissionModel.builder()
                .application(permission.getApplication())
                .role(permission.getRole())
                .restrictions(permission.getPermissionRestrictions().stream()
                    .map(restriction -> UserAdministrationPermissionRestrictionModel.builder()
                        .value(restriction.getValueAsString())
                        .restrictionType(restriction.getType())
                    .build()).collect(Collectors.toSet()))
                .build())
        .collect(Collectors.toSet());
    return UserAdministrationModel.builder()
        .userId(userModel.getUserId())
        .permissions(permissionModels)
        .build();
  }
}
