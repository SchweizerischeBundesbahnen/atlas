package ch.sbb.atlas.user.administration.module.manualmail.repository;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManualMailOverrideRepository extends JpaRepository<UserManualMailOverride, Long> {

  Optional<UserManualMailOverride> findBySbbUserIdIgnoreCase(String sbbUserId);

  List<UserManualMailOverride> findAllBySbbUserIdInIgnoreCase(Collection<String> sbbUserIds);

  Optional<UserManualMailOverride> findByMailIgnoreCase(String mail);

  void deleteBySbbUserIdIgnoreCase(String sbbUserId);

}
