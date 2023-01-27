package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
class ServicePointNumberEqualsSpec implements Specification<ServicePointVersion> {

  private final ServicePointNumber servicePointNumber;

  @Override
  public Predicate toPredicate(Root<ServicePointVersion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.equal(root.get(Fields.number), servicePointNumber);
  }

}
