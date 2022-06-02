package ch.sbb.atlas.searching.specification;

import ch.sbb.atlas.searching.predicates.StringPredicates;
import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SearchCriteriaSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final List<String> searchCriteria;
  private final List<String> searchPaths;

  public SearchCriteriaSpecification(List<String> searchCriteria,
      List<String> searchPaths) {
    this.searchCriteria = Objects.requireNonNull(searchCriteria);
    this.searchPaths = searchPaths;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.and(searchCriteria.stream().map(searchString -> criteriaBuilder.or(
        searchPaths.stream()
                   .map(path -> StringPredicates.likeIgnoreCase(criteriaBuilder, root.get(path).as(String.class),
                       searchString))
                   .toArray(Predicate[]::new))
    ).toArray(Predicate[]::new));
  }
}