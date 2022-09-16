package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.user.administration.entity.UserPermission;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

  @Query("select distinct up.sbbUserId from user_permission up")
  Page<String> findAllDistinctSbbUserId(Pageable pageable);

  List<UserPermission> findBySbbUserIdIgnoreCase(String sbbUserId);

  boolean existsBySbbUserIdIgnoreCase(String userId);
}
