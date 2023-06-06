package ch.sbb.atlas.user.administration.security.repository;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

  Optional<Permission> findByIdentifierAndApplication(String identifier, ApplicationType applicationType);

  List<Permission> findAllByIdentifier(String identifier);
}
