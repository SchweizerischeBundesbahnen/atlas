package ch.sbb.business.organisation.directory.repository;

import ch.sbb.business.organisation.directory.entity.BoTcLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoTcLinkRepository extends JpaRepository<BoTcLink, Integer> {

}
