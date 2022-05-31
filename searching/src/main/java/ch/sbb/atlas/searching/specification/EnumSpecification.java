package ch.sbb.atlas.searching.specification;

import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

public class EnumSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final List<?> enumRestrictions;
  private final SingularAttribute<T, ?> enumAttribute;

  public EnumSpecification(List<?> enumRestrictions, SingularAttribute<T, ?> enumAttribute) {
    this.enumRestrictions = Objects.requireNonNull(enumRestrictions);
    this.enumAttribute = enumAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (enumRestrictions.isEmpty()) {
      return criteriaBuilder.and();
    }
    return root.get(enumAttribute).in(enumRestrictions);
  }
}