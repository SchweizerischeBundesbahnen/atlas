package ch.sbb.atlas.searching.predicates;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public final class StringPredicates {

  private StringPredicates(){
    throw new IllegalArgumentException();
  }

  public static Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Path<String> path,
      String searchString) {
    return criteriaBuilder.like(criteriaBuilder.lower(path),
        "%" + searchString.toLowerCase() + "%");
  }
}
