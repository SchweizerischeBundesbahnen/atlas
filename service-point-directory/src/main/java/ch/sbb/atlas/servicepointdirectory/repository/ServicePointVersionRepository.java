package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.util.List;
import org.locationtech.jts.geom.Envelope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends JpaRepository<ServicePointVersion, Long>,
    JpaSpecificationExecutor<ServicePointVersion> {

  List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

  boolean existsByNumber(ServicePointNumber servicePointNumber);
  
  static Specification<ServicePointVersion> coordinatesBetween(
      SpatialReference spatialReference,
      Envelope envelope) {
    return spatialReferenceEquals(spatialReference)
        .and(eastBetween(envelope.getMinX(), envelope.getMaxX())
            .and(northBetween(envelope.getMinY(), envelope.getMaxY()))
        );
  }

  static Specification<ServicePointVersion> validAtDate(LocalDate date) {
    return validFromBeforeDate(date).and(validToAfterDate(date));
  }

  static Specification<ServicePointVersion> validFromBeforeDate(LocalDate date) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
        root.get("validFrom"), date);
  }

  static Specification<ServicePointVersion> validToAfterDate(LocalDate date) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
        root.get("validTo"), date);
  }

  static Specification<ServicePointVersion> spatialReferenceEquals(
      SpatialReference spatialReference) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
        root.get("servicePointGeolocation").get("spatialReference"), spatialReference);
  }

  static Specification<ServicePointVersion> eastBetween(Double west, Double east) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.between(
        root.get("servicePointGeolocation").get("east"), west, east);
  }

  static Specification<ServicePointVersion> northBetween(Double south, Double north) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.between(
        root.get("servicePointGeolocation").get("north"), south, north);
  }

}
