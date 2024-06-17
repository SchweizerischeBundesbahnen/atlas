package ch.sbb.workflow.repository;

import ch.sbb.workflow.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

  Otp findByPersonId(Long personId);

}
