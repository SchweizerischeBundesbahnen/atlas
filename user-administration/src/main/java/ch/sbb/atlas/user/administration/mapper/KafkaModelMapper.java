package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.PermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import java.util.Set;
import java.util.stream.Collectors;

public class KafkaModelMapper {

  public static UserAdministrationModel toKafkaModel(UserModel userModel) {
    Set<UserAdministrationPermissionModel> permissionModels = userModel.getPermissions().stream()
        .map(
            permission -> UserAdministrationPermissionModel.builder()
                .application(permission.getApplication())
                .role(permission.getRole())
                .sboids(permission.getPermissionRestrictions().stream()
                    .filter(i -> i.getType() == PermissionRestrictionType.BUSINESS_ORGANISATION)
                    .map(PermissionRestrictionModel::getValueAsString)
                    .collect(Collectors.toSet()))
                .swissCantons(permission.getPermissionRestrictions().stream()
                    .filter(i -> i.getType() == PermissionRestrictionType.CANTON)
                    .map(model -> SwissCanton.valueOf(model.getValueAsString()))
                    .collect(Collectors.toSet()))
                .build())
        .collect(Collectors.toSet());
    return UserAdministrationModel.builder()
        .sbbUserId(userModel.getSbbUserId())
        .permissions(permissionModels)
        .build();
  }
}
