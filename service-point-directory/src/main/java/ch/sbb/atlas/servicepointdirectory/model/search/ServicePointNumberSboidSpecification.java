package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServicePointNumberSboidSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final List<String> sboids;

  private final List<Integer> shortNumbers;

  private final List<ServicePointNumber> servicePointNumbers;

  private final List<Country> countries;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Subquery<?> servicePointSubquery = query.subquery(ServicePointVersion.class);
    Root<ServicePointVersion> fromServicePoint = servicePointSubquery.from(ServicePointVersion.class);
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(fromServicePoint.get("number"), root.get("servicePointNumber")));
    if (!sboids.isEmpty()) {
      predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.businessOrganisation).in(sboids)));
    }
    if (!shortNumbers.isEmpty()) {
      predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.numberShort).in(shortNumbers)));
    }
    if (!servicePointNumbers.isEmpty()) {
      predicates.add(criteriaBuilder.and(fromServicePoint.get("number").in(servicePointNumbers)));
    }
    if (!countries.isEmpty()) {
      predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.country).in(countries)));
    }
    servicePointSubquery.where(predicates.toArray(new Predicate[]{}));
    return criteriaBuilder.exists(servicePointSubquery);
  }
}
