package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends
    JpaRepository<ServicePointVersion, Long>, JpaSpecificationExecutor<ServicePointVersion> {

  List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

  boolean existsByNumber(ServicePointNumber servicePointNumber);
  
  static Specification<ServicePointVersion> coordinatesBetween(
      Double west,
      Double south,
      Double east,
      Double north) {
    return eastBetween(east, west).and(northBetween(south, north));
  }

  static Specification<ServicePointVersion> eastBetween(Double east, Double west) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.
        between(root.get("servicePointGeolocation").get("east"), west, east);
  }

  static Specification<ServicePointVersion> northBetween(Double south, Double north) {
    return (root, query, criteriaBuilder) -> criteriaBuilder
        .between(root.get("servicePointGeolocation").get("north"), south, north);
  }

}
