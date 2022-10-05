package ch.sbb.atlas.searching.specification;

import java.io.Serial;
import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class InSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final List<String> searchStrings;
  private final String stringAttribute;

  public InSpecification(List<String> searchStrings,
      String stringAttribute) {
    this.searchStrings = Objects.requireNonNull(searchStrings);
    this.stringAttribute = stringAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (searchStrings.isEmpty()) {
      return criteriaBuilder.and();
    }
    return root.get(stringAttribute).in(searchStrings);
  }
}