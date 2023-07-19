package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficPointElementVersionRepository extends
    JpaRepository<TrafficPointElementVersion, Long>, JpaSpecificationExecutor<TrafficPointElementVersion> {

  @EntityGraph(attributePaths = {Fields.trafficPointElementGeolocation})
  List<TrafficPointElementVersion> findAllBySloidOrderByValidFrom(String sloid);

//TODO: Only if we do it with SQL Join
//  @Override
//  @Query(value = " SELECT trp.id, "
//      + "trp.sloid, trp.parent_sloid, "
//      + "trp.designation, trp.designation_operational, "
//      + "trp.traffic_point_element_type, trp.length,  "
//      + "trp.boarding_area_height, trp.compass_direction, "
//      + "trp.service_point_number, trp.valid_from, "
//      + "trp.valid_to, "
//      + "trp.traffic_point_geolocation_id, "
//      + "trp.creation_date, "
//      + "trp.creator, "
//      + "trp.edition_date, "
//      + "trp.editor, "
//      + "trp.version, "
//      + "spv.sloid as servicePointSloid, "
//      + "spv.number_short, "
//      + "spv.country, "
//      + "spv.business_organisation as sboid "
//      + "from traffic_point_element_version trp left join service_point_version spv on trp.service_point_number = spv.number "
//      + "and (case when current_date between spv.valid_from and spv.valid_to then 0 else 1 end = 0)", nativeQuery = true)
//  Page<TrafficPointElementVersion> findAll(Specification<TrafficPointElementVersion> spec, Pageable pageable);

  boolean existsBySloid(String sloid);

  @Modifying(clearAutomatically = true)
  @Query("update traffic_point_element_version v set v.version = (v.version + 1) where v.sloid = :sloid")
  void incrementVersion(@Param("sloid") String sloid);
}
