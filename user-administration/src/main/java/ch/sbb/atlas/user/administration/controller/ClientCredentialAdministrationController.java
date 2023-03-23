package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.user.administration.ClientCredentialAdministrationApiV1;
import ch.sbb.atlas.api.user.administration.ClientCredentialModel;
import ch.sbb.atlas.api.user.administration.ClientCredentialPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.mapper.ClientCredentialMapper;
import ch.sbb.atlas.user.administration.service.ClientCredentialAdministrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientCredentialAdministrationController implements ClientCredentialAdministrationApiV1 {

  private final ClientCredentialAdministrationService clientCredentialAdministrationService;

  @Override
  public Container<ClientCredentialModel> getClientCredentials(Pageable pageable) {
    Page<ClientCredentialPermission> clientCredentialPermissions =
        clientCredentialAdministrationService.getClientCredentialPermissions(
        pageable);
    return Container.<ClientCredentialModel>builder()
        .totalCount(clientCredentialPermissions.getTotalElements())
        .objects(ClientCredentialMapper.toModel(clientCredentialPermissions.getContent()))
        .build();
  }

  @Override
  public ClientCredentialModel getClientCredential(String clientId) {
    return ClientCredentialMapper.toSingleModel(clientCredentialAdministrationService.getClientCredentialPermission(clientId));
  }

  @Override
  public ClientCredentialModel createClientCredential(ClientCredentialPermissionCreateModel client) {
    return null;
  }

  @Override
  public ClientCredentialModel updateClientCredential(ClientCredentialPermissionCreateModel editedPermissions) {
    return null;
  }
}
