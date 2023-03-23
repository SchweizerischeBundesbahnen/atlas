package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.repository.ClientCredentialPermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientCredentialAdministrationService {

  private final ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  public Page<ClientCredentialPermission> getClientCredentialPermissions(Pageable pageable) {
    return clientCredentialPermissionRepository.findAll(pageable);
  }

  public List<ClientCredentialPermission> getClientCredentialPermission(String clientId) {
    return clientCredentialPermissionRepository.findAllByClientCredentialId(clientId);
  }

  public ClientCredentialPermission save(ClientCredentialPermission clientCredentialPermission) {
    String clientCredentialId = clientCredentialPermission.getClientCredentialId();
    if (clientCredentialPermissionRepository.existsByClientCredentialId(clientCredentialId)) {
      throw new UserPermissionConflictException(clientCredentialId);
    }
    return clientCredentialPermissionRepository.save(clientCredentialPermission);
  }

  public void update(String clientCredentialId) {
    List<ClientCredentialPermission> existingPermissions =
        clientCredentialPermissionRepository.findAllByClientCredentialId(clientCredentialId);

    existingPermissions.forEach(existingPermission -> {

    });
  }

}
