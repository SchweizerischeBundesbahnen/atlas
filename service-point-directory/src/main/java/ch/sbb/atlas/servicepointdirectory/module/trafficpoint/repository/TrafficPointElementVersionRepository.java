package ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository;

import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficPointElementVersionRepository extends
    JpaRepository<TrafficPointElementVersion, Long>, JpaSpecificationExecutor<TrafficPointElementVersion> {

  @EntityGraph(attributePaths = {TrafficPointElementVersion.Fields.trafficPointElementGeolocation})
  List<TrafficPointElementVersion> findAllBySloidOrderByValidFrom(String sloid);

  boolean existsBySloid(String sloid);

  @Modifying(clearAutomatically = true)
  @Query("update traffic_point_element_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);

}
