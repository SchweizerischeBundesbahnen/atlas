package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serial;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class BooleanSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

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
