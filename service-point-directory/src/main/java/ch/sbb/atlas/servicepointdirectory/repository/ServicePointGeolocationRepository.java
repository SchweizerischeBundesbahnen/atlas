package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import java.time.LocalDate;
import org.locationtech.jts.geom.Envelope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointGeolocationRepository extends
    JpaRepository<ServicePointGeoData, Long>,
    JpaSpecificationExecutor<ServicePointGeoData> {

  static Specification<ServicePointGeoData> coordinatesBetween(
      SpatialReference spatialReference,
      Envelope envelope) {
    return spatialReferenceEquals(spatialReference)
        .and(eastBetween(envelope.getMinX(), envelope.getMaxX())
            .and(northBetween(envelope.getMinY(), envelope.getMaxY()))
        );
  }

  static Specification<ServicePointGeoData> validAtDate(LocalDate date) {
    return validFromBeforeDate(date).and(validToAfterDate(date));
  }

  static Specification<ServicePointGeoData> validFromBeforeDate(LocalDate date) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
        root.get("validFrom"), date);
  }

  static Specification<ServicePointGeoData> validToAfterDate(LocalDate date) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
        root.get("validTo"), date);
  }

  static Specification<ServicePointGeoData> spatialReferenceEquals(
      SpatialReference spatialReference) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
        root.get("spatialReference"), spatialReference);
  }

  static Specification<ServicePointGeoData> eastBetween(Double west, Double east) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.between(
        root.get("east"), west, east);
  }

  static Specification<ServicePointGeoData> northBetween(Double south, Double north) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.between(
        root.get("north"), south, north);
  }

}
