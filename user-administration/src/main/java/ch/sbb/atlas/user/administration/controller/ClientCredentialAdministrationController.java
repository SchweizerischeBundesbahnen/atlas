package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.user.administration.ClientCredentialAdministrationApiV1;
import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.api.user.administration.ClientCredentialModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.mapper.ClientCredentialMapper;
import ch.sbb.atlas.user.administration.mapper.KafkaModelMapper;
import ch.sbb.atlas.user.administration.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.service.UserPermissionDistributor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientCredentialAdministrationController implements ClientCredentialAdministrationApiV1 {

  private final ClientCredentialAdministrationService clientCredentialAdministrationService;
  private final UserPermissionDistributor userPermissionDistributor;

  @Override
  public Container<ClientCredentialModel> getClientCredentials(Pageable pageable) {
    List<ClientCredentialPermission> clientCredentialPermissions =
        clientCredentialAdministrationService.getClientCredentialPermissions();
    List<ClientCredentialModel> clientCredentials = ClientCredentialMapper.toModel(clientCredentialPermissions);
    return OverviewDisplayBuilder.toPagedContainer(clientCredentials, pageable);
  }

  @Override
  public ClientCredentialModel getClientCredential(String clientId) {
    return ClientCredentialMapper.toSingleModel(clientCredentialAdministrationService.getClientCredentialPermission(clientId));
  }

  @Override
  public ClientCredentialModel createClientCredential(ClientCredentialCreateModel client) {
    ClientCredentialModel clientCredentialModel = ClientCredentialMapper.toSingleModel(
        clientCredentialAdministrationService.create(client));
    userPermissionDistributor.pushUserPermissionToKafka(KafkaModelMapper.toKafkaModel(clientCredentialModel));
    return clientCredentialModel;
  }

  @Override
  public ClientCredentialModel updateClientCredential(String clientId, ApplicationType application,
      PermissionModel editedPermissions) {
    clientCredentialAdministrationService.update(clientId, application, editedPermissions);
    ClientCredentialModel clientCredentialModel = getClientCredential(clientId);
    userPermissionDistributor.pushUserPermissionToKafka(KafkaModelMapper.toKafkaModel(clientCredentialModel));
    return clientCredentialModel;
  }
}
