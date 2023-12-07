package ch.sbb.prm.directory.repository;

import ch.sbb.prm.directory.entity.TicketCounterVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCounterRepository extends JpaRepository<TicketCounterVersion, Long>,
    JpaSpecificationExecutor<TicketCounterVersion> {

  List<TicketCounterVersion> findByParentServicePointSloid(String parentServicePointSloid);

  List<TicketCounterVersion> findAllBySloidOrderByValidFrom(String sloid);

  @Modifying(clearAutomatically = true)
  @Query("update ticket_counter_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);
}
