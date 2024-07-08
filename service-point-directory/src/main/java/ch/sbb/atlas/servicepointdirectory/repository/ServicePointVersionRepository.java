package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointVersionRepository extends JpaRepository<ServicePointVersion, Long>,
    JpaSpecificationExecutor<ServicePointVersion> {

  @EntityGraph(attributePaths = {ServicePointVersion.Fields.servicePointGeolocation, ServicePointVersion.Fields.categories,
      ServicePointVersion.Fields.meansOfTransport})
  List<ServicePointVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

  List<ServicePointVersion> findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(ServicePointNumber number);

  boolean existsByNumber(ServicePointNumber servicePointNumber);

  @Modifying(clearAutomatically = true)
  @Query("update service_point_version v set v.version = (v.version + 1) where v.number = :number")
  void incrementVersion(@Param("number") ServicePointNumber number);

  @EntityGraph(attributePaths = {ServicePointVersion.Fields.servicePointGeolocation, ServicePointVersion.Fields.categories,
      ServicePointVersion.Fields.meansOfTransport})
  List<ServicePointVersion> findAllByIdIn(Collection<Long> ids, Sort sort);

  default Page<ServicePointVersion> loadByIdsFindBySpecification(Specification<ServicePointVersion> specification,
      Pageable pageable) {
    Function<FetchableFluentQuery<ServicePointVersion>, Page<IdProjection>> idProjection =
        i -> i.project("id").as(IdProjection.class).sortBy(pageable.getSort()).page(pageable);
    Page<IdProjection> pagedIds = findBy(specification, idProjection);

    List<Long> idList = pagedIds.getContent().stream().map(IdProjection::getId).toList();
    List<ServicePointVersion> loadedObjectsById = findAllByIdIn(idList, pageable.getSort());
    return new PageImpl<>(loadedObjectsById, pagedIds.getPageable(), pagedIds.getTotalElements());
  }

  default List<ServicePointVersion> findDesignationOfficialOverlaps(ServicePointVersion servicePointVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndDesignationOfficialIgnoreCase(
        servicePointVersion.getValidFrom(), servicePointVersion.getValidTo(),
        servicePointVersion.getDesignationOfficial())
        .stream()
        .filter(i -> !i.getNumber().equals(servicePointVersion.getNumber()))
        .filter(i -> i.getCountry() == servicePointVersion.getCountry())
        .filter(i -> i.getStatus() != Status.REVOKED)
        .toList();
  }

  List<ServicePointVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndDesignationOfficialIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String designationOfficial);

  default List<ServicePointVersion> findDesignationLongOverlaps(ServicePointVersion servicePointVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndDesignationLongIgnoreCase(
        servicePointVersion.getValidFrom(), servicePointVersion.getValidTo(),
        servicePointVersion.getDesignationLong())
        .stream()
        .filter(i -> !i.getNumber().equals(servicePointVersion.getNumber()))
        .filter(i -> i.getCountry() == servicePointVersion.getCountry())
        .filter(i -> i.getStatus() != Status.REVOKED)
        .toList();
  }

  List<ServicePointVersion> findServicePointVersionByAbbreviation(String abbreviation);

  List<ServicePointVersion> findBySloidOrderByValidFrom(String sloid);

  List<ServicePointVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndDesignationLongIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String designationLong);

}
