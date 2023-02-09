package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class InSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final List<?> searchRestrictions;
  private final String stringAttribute;

  public InSpecification(List<?> searchRestrictions,
      String stringAttribute) {
    this.searchRestrictions = Objects.requireNonNull(searchRestrictions);
    this.stringAttribute = stringAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (searchRestrictions.isEmpty()) {
      return criteriaBuilder.and();
    }
    return root.get(stringAttribute).in(searchRestrictions);
  }
}
