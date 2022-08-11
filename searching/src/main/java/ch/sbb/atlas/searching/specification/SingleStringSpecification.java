package ch.sbb.atlas.searching.specification;

import ch.sbb.atlas.searching.predicates.StringPredicates;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SingleStringSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final Optional<String> searchString;
  private final String stringAttribute;

  public SingleStringSpecification(Optional<String> searchString,
      String stringAttribute) {
    this.searchString = Objects.requireNonNull(searchString);
    this.stringAttribute = stringAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (searchString.isEmpty()) {
      return criteriaBuilder.and();
    }
    return StringPredicates.likeIgnoreCase(criteriaBuilder, root.get(stringAttribute),
        searchString.orElseThrow());
  }
}