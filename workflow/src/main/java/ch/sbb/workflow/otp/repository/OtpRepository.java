package ch.sbb.workflow.otp.repository;

import ch.sbb.workflow.otp.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

  Otp findByPersonId(Long personId);

}
