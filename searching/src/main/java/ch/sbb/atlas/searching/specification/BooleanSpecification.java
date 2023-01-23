package ch.sbb.atlas.searching.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class BooleanSpecification<T> implements Specification<T> {

  private final SingularAttribute<T, Boolean> attribute;
  private final Boolean value;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (value == null) {
      return criteriaBuilder.and();
    }
    return criteriaBuilder.equal(root.get(attribute), value);
  }
}
