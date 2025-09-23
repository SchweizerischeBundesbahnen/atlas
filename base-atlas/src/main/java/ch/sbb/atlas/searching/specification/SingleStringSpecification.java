package ch.sbb.atlas.searching.specification;

import ch.sbb.atlas.searching.predicates.StringPredicates;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class SingleStringSpecification<T> implements Specification<T> {

  @Serial private static final long serialVersionUID = 1;

  private final String searchString;
  private final String stringAttribute;

  public SingleStringSpecification(String searchString, String stringAttribute) {
    this.searchString = searchString;
    this.stringAttribute = stringAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (Objects.isNull(searchString)) {
      return criteriaBuilder.and();
    }
    return StringPredicates.likeIgnoreCase(criteriaBuilder, root.get(stringAttribute), searchString);
  }
}
