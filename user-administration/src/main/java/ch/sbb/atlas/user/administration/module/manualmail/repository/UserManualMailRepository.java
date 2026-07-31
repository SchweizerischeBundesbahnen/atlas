package ch.sbb.atlas.user.administration.module.manualmail.repository;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMail;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserManualMailRepository extends JpaRepository<UserManualMail, Long> {

  Optional<UserManualMail> findBySbbUserIdIgnoreCase(String sbbUserId);

  List<UserManualMail> findAllBySbbUserIdInIgnoreCase(Collection<String> sbbUserIds);

  Optional<UserManualMail> findByMailIgnoreCase(String mail);

  void deleteBySbbUserIdIgnoreCase(String sbbUserId);

}
