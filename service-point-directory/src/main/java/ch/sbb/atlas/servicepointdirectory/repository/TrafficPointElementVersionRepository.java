package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficPointElementVersionRepository extends
    JpaRepository<TrafficPointElementVersion, Long>, JpaSpecificationExecutor<TrafficPointElementVersion> {

  List<TrafficPointElementVersion> findAllBySloidOrderByValidFrom(String sloid);
}
