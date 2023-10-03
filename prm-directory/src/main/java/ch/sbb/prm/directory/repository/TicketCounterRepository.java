package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.TicketCounterVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCounterRepository extends JpaRepository<TicketCounterVersion, String>,
    JpaSpecificationExecutor<TicketCounterVersion> {

}
