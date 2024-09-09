package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
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
