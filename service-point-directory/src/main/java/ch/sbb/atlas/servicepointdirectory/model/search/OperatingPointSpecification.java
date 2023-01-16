package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class OperatingPointSpecification implements Specification<ServicePointVersion> {

  private final Boolean operatingPoint;

  @Override
  public Predicate toPredicate(Root<ServicePointVersion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (operatingPoint == null) {
      return criteriaBuilder.and();
    }
    Predicate isOperatingPointPredicate = getIsOperatingPointPredicate(root, criteriaBuilder);
    return operatingPoint ? isOperatingPointPredicate : isOperatingPointPredicate.not();
  }

  private Predicate getIsOperatingPointPredicate(Root<ServicePointVersion> root, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.or(
        root.get(ServicePointVersion_.operatingPointType).isNotNull(),
        criteriaBuilder.isNotEmpty(root.get(ServicePointVersion_.meansOfTransport)),
        root.get(ServicePointVersion_.sortCodeOfDestinationStation).isNotNull());
  }
}
