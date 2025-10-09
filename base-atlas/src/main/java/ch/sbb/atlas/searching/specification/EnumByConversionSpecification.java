package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

public class EnumByConversionSpecification<T, U, E> implements Specification<T> {

  @Serial private static final long serialVersionUID = 1;

  private final transient List<U> parameterRestrictions;
  private final transient Function<U, E> parameterToEnumFunction;
  private final transient SingularAttribute<T, E> enumAttribute;
  private final Boolean notIn;

  public EnumByConversionSpecification(List<U> parameterRestrictions, Function<U, E> parameterToEnumFunction,
      SingularAttribute<T, E> enumAttribute) {
    this(parameterRestrictions, parameterToEnumFunction, enumAttribute, false);
  }

  public EnumByConversionSpecification(List<U> parameterRestrictions, Function<U, E> parameterToEnumFunction,
      SingularAttribute<T, E> enumAttribute, Boolean notIn) {
    this.parameterRestrictions = Objects.requireNonNull(parameterRestrictions);
    this.enumAttribute = enumAttribute;
    this.notIn = notIn;
    this.parameterToEnumFunction = parameterToEnumFunction;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (parameterRestrictions.isEmpty()) {
      return criteriaBuilder.and();
    }
    List<E> enumRestrictions = parameterRestrictions.stream().map(parameterToEnumFunction).toList();
    if (enumRestrictions.stream().allMatch(Objects::isNull)) {
      return criteriaBuilder.or();
    }
    return notIn ? getPathSingular(root).in(enumRestrictions).not() : getPathSingular(root).in(enumRestrictions);
  }

  Path<E> getPathSingular(Root<T> root) {
    return root.get(enumAttribute);
  }
}
