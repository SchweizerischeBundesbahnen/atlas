package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class EnumSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final List<?> enumRestrictions;
  private final SingularAttribute<T, ?> enumAttribute;
  private final Boolean notIn;

  public EnumSpecification(List<?> enumRestrictions, SingularAttribute<T, ?> enumAttribute) {
    this.enumRestrictions = Objects.requireNonNull(enumRestrictions);
    this.enumAttribute = enumAttribute;
    this.notIn = false;
  }

  public EnumSpecification(List<?> enumRestrictions, SingularAttribute<T, ?> enumAttribute, Boolean notIn) {
    this.enumRestrictions = Objects.requireNonNull(enumRestrictions);
    this.enumAttribute = enumAttribute;
    this.notIn = notIn;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (enumRestrictions.isEmpty()) {
      return criteriaBuilder.and();
    }
    return notIn ? root.get(enumAttribute).in(enumRestrictions).not() : root.get(enumAttribute).in(enumRestrictions);
  }
}
