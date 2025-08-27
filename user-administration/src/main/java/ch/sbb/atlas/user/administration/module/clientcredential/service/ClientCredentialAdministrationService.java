package ch.sbb.atlas.user.administration.module.clientcredential.service;

import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.mapper.PermissionRestrictionMapper;
import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.module.clientcredential.mapper.ClientCredentialMapper;
import ch.sbb.atlas.user.administration.module.clientcredential.mapper.ClientCredentialPermissionCreateMapper;
import ch.sbb.atlas.user.administration.module.clientcredential.repository.ClientCredentialPermissionRepository;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientCredentialAdministrationService {

  private final ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  public List<ClientCredentialPermission> getClientCredentialPermissions() {
    return clientCredentialPermissionRepository.findAll();
  }

  public List<ClientCredentialPermission> getClientCredentialPermission(String clientId) {
    return clientCredentialPermissionRepository.findAllByClientCredentialId(clientId);
  }

  public List<ClientCredentialPermission> create(ClientCredentialCreateModel createModel) {
    String clientCredentialId = createModel.getClientCredentialId();
    if (clientCredentialPermissionRepository.existsByClientCredentialId(clientCredentialId)) {
      throw new UserPermissionConflictException(clientCredentialId);
    }
    return clientCredentialPermissionRepository.saveAll(ClientCredentialPermissionCreateMapper.toEntityList(createModel));
  }

  public void update(String clientId, ApplicationType application, PermissionModel editedPermission) {
    List<ClientCredentialPermission> existingPermissions = getClientCredentialPermission(clientId);

    Optional<ClientCredentialPermission> existingPermission =
        existingPermissions.stream().filter(i -> i.getApplication() == application).findFirst();

    if (existingPermission.isPresent()) {
      updateExistingPermission(editedPermission, existingPermission.get());
    } else {
      ClientCredentialPermission credentialPermission = existingPermissions.getFirst();
      ClientCredentialPermission additionalPermission = ClientCredentialMapper.toEntity(editedPermission, credentialPermission);
      clientCredentialPermissionRepository.save(additionalPermission);
    }
  }

  private void updateExistingPermission(PermissionModel editedPermission, ClientCredentialPermission existingPermission) {
    existingPermission.setRole(editedPermission.getRole());

    existingPermission.getPermissionRestrictions().clear();
    Set<PermissionRestriction> permissionRestrictions = editedPermission.getPermissionRestrictions().stream().map(
            restriction -> PermissionRestrictionMapper.toEntity(existingPermission, restriction))
        .collect(Collectors.toSet());
    existingPermission.getPermissionRestrictions().addAll(permissionRestrictions);
    existingPermission.setEditionDate(LocalDateTime.now());
  }

}
