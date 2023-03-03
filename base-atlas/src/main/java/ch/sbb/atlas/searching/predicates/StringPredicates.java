package ch.sbb.atlas.searching.predicates;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class StringPredicates {

  private static final String UNDERSCORE = "_";
  private static final String PERCENT = "%";

  public static Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Expression<String> path,
      String searchString) {
    String escapedSearchString = searchString;
    if (searchString.contains(UNDERSCORE)) {
      escapedSearchString = escapedSearchString.replace(UNDERSCORE, "\\_");
    }
    if (escapedSearchString.contains(PERCENT)) {
      escapedSearchString = escapedSearchString.replace(PERCENT, "\\%");
    }
    return criteriaBuilder.like(criteriaBuilder.lower(path),
        PERCENT + escapedSearchString.toLowerCase() + PERCENT, '\\');
  }
}
