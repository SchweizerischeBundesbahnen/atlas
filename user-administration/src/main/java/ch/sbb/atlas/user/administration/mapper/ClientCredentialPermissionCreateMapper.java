package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.ClientCredentialPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientCredentialPermissionCreateMapper {

  public static List<ClientCredentialPermission> toEntityList(
      ClientCredentialPermissionCreateModel clientCredentialPermissionCreateModel) {
    return clientCredentialPermissionCreateModel.getPermissions().stream().map(permission -> {
          ClientCredentialPermission clientCredentialPermission = ClientCredentialPermission.builder()
              .clientCredentialId(clientCredentialPermissionCreateModel.getClientCredentialId())
              .alias(clientCredentialPermissionCreateModel.getAlias())
              .comment(clientCredentialPermissionCreateModel.getComment())
              .application(permission.getApplication())
              .role(permission.getRole())
              .build();
          clientCredentialPermission.setPermissionRestrictions(permission.getPermissionRestrictions().stream().map(restriction ->
                  PermissionRestriction.builder()
                      .clientCredentialPermission(clientCredentialPermission)
                      .type(restriction.getType())
                      .restriction(restriction.getValueAsString())
                      .build())
              .collect(Collectors.toSet()));
          return clientCredentialPermission;
        }
    ).toList();
  }
}
