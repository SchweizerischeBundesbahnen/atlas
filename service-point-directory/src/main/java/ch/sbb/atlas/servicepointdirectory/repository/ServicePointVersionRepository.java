package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends JpaRepository<ServicePointVersion, Long>,
    JpaSpecificationExecutor<ServicePointVersion> {

  @EntityGraph(attributePaths = {Fields.servicePointGeolocation, Fields.categories, Fields.meansOfTransport})
  List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

  @Override
  @EntityGraph(attributePaths = {Fields.servicePointGeolocation}, type = EntityGraphType.LOAD)
  Page<ServicePointVersion> findAll(Specification specification, Pageable pageable);

  boolean existsByNumber(ServicePointNumber servicePointNumber);

}
