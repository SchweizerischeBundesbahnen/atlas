package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class EnumSpecification<T, E> implements Specification<T> {

  @Serial private static final long serialVersionUID = 1;

  private final transient List<E> enumRestrictions;
  private final String enumAttribute;
  private final boolean notIn;

  public EnumSpecification(E enumRestriction, SingularAttribute<T, E> enumAttribute) {
    this(enumRestriction == null ? Collections.emptyList() : Collections.singletonList(enumRestriction), enumAttribute.getName(),
        false);
  }

  public EnumSpecification(List<E> enumRestrictions, SingularAttribute<? super T, E> enumAttribute) {
    this(enumRestrictions, enumAttribute.getName(), false);
  }

  public EnumSpecification(List<E> enumRestrictions, String enumAttribute) {
    this(enumRestrictions, enumAttribute, false);
  }

  public EnumSpecification(List<E> enumRestrictions, String enumAttribute, Boolean notIn) {
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
    return notIn ? root.get(enumAttribute).in(enumRestrictions).not() :
        root.get(enumAttribute).in(enumRestrictions);
  }
}
