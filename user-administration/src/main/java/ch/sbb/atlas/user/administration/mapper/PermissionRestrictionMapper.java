package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.PermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PermissionRestrictionMapper {

  public static PermissionRestrictionModel<?> toModel(PermissionRestriction permissionRestriction) {
    PermissionRestrictionModel<?> restrictionModel = switch (permissionRestriction.getType()) {
      case CANTON -> new CantonPermissionRestrictionModel();
      case BUSINESS_ORGANISATION -> new SboidPermissionRestrictionModel();
    };
    restrictionModel.setValueAsString(permissionRestriction.getRestriction());
    return restrictionModel;
  }

  public static PermissionRestriction toEntity(UserPermission userPermission,
      PermissionRestrictionModel<?> permissionRestrictionModel) {
    return PermissionRestriction.builder()
        .userPermission(userPermission)
        .type(permissionRestrictionModel.getType())
        .restriction(permissionRestrictionModel.getValueAsString())
        .build();
  }

}
