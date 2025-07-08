package ch.sbb.atlas.user.administration.mapper;

import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientCredentialPermissionCreateMapper {

  public static List<ClientCredentialPermission> toEntityList(ClientCredentialCreateModel clientCredentialCreateModel) {
    Stream<ClientCredentialPermission> clientCredentialPermissions = Arrays.stream(ApplicationType.values()).map(application ->
        ClientCredentialPermission.builder()
            .clientCredentialId(clientCredentialCreateModel.getClientCredentialId())
            .alias(clientCredentialCreateModel.getAlias())
            .comment(clientCredentialCreateModel.getComment())
            .application(application)
            .role(ApplicationRole.READER)
            .build()
    );
    return clientCredentialPermissions.toList();
  }
}
