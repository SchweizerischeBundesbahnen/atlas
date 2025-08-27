package ch.sbb.atlas.user.administration.module.useradministration.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.repository.CustomUserPermissionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long>, CustomUserPermissionRepository {

  List<UserPermission> findBySbbUserIdIgnoreCase(String sbbUserId);

  Optional<UserPermission> findBySbbUserIdIgnoreCaseAndApplication(String sbbUserId, ApplicationType applicationType);

  boolean existsBySbbUserIdIgnoreCase(String userId);
}
