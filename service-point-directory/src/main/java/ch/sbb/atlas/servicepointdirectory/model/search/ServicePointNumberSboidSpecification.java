package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.io.Serial;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ServicePointNumberSboidSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final List<String> sboids;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (sboids.isEmpty()) {
      return criteriaBuilder.and();
    }
    Subquery<?> servicePointSubquery = query.subquery(ServicePointVersion.class);
    Root<ServicePointVersion> fromServicePoint = servicePointSubquery.from(ServicePointVersion.class);
    servicePointSubquery.where(criteriaBuilder.and(
        criteriaBuilder.equal(fromServicePoint.get("number"), root.get("servicePointNumber")),
        fromServicePoint.get(ServicePointVersion_.businessOrganisation).in(sboids)));
    return criteriaBuilder.exists(servicePointSubquery);
  }
}
