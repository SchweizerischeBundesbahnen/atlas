package ch.sbb.atlas.user.administration.module.clientcredential.repository;

import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientCredentialPermissionRepository extends JpaRepository<ClientCredentialPermission, Long> {

  boolean existsByClientCredentialId(String clientCredentialId);

  List<ClientCredentialPermission> findAllByClientCredentialId(String clientCredentialId);
}
