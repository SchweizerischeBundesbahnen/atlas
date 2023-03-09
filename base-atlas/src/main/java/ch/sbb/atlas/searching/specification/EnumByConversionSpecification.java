package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

public class EnumByConversionSpecification<T, U> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final List<U> parameterRestrictions;
  private final Function<U, ?> parameterToEnumFunction;
  private final SingularAttribute<T, ?> enumAttribute;
  private final Boolean notIn;

  public EnumByConversionSpecification(List<U> parameterRestrictions, Function<U, ?> parameterToEnumFunction, SingularAttribute<T, ?> enumAttribute) {
    this(parameterRestrictions, parameterToEnumFunction, enumAttribute, false);
  }

  public EnumByConversionSpecification(List<U> parameterRestrictions, Function<U, ?> parameterToEnumFunction, SingularAttribute<T, ?> enumAttribute, Boolean notIn) {
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
    List<?> enumRestrictions = parameterRestrictions.stream().map(parameterToEnumFunction).toList();
    if (enumRestrictions.stream().allMatch(Objects::isNull)) {
      return criteriaBuilder.or();
    }
    return notIn ? root.get(enumAttribute).in(enumRestrictions).not() : root.get(enumAttribute).in(enumRestrictions);
  }
}
