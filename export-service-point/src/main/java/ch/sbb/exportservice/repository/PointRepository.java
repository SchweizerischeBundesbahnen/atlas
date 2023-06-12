package ch.sbb.exportservice.repository;

import static ch.sbb.exportservice.entity.ServicePointVersion.Fields.categories;
import static ch.sbb.exportservice.entity.ServicePointVersion.Fields.meansOfTransport;
import static ch.sbb.exportservice.entity.ServicePointVersion.Fields.servicePointGeolocation;

import ch.sbb.exportservice.entity.ServicePointVersion;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface PointRepository extends JpaRepository<ServicePointVersion, Long> {

  @EntityGraph(attributePaths = {servicePointGeolocation, categories, meansOfTransport})
  @Override
  List<ServicePointVersion> findAll();

  @Query("select spv from service_point_version spv"
      + " join fetch spv.categories spvc"
      + " join fetch spv.servicePointGeolocation spvg"
      + " join fetch spv.meansOfTransport spvmot")
  List<ServicePointVersion> findAllServicePoint();

}
