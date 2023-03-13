package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.function.Function;

public class EnumByConversionServicePointGeolocationSpecification<T, U> extends EnumByConversionSpecification<T,U> {

  private final SingularAttribute<?, ?> deepEnumAttribute;

  public EnumByConversionServicePointGeolocationSpecification(List<U> parameterRestrictions, Function<U, ?> parameterToEnumFunction, SingularAttribute<T, ?> enumAttribute, SingularAttribute<?, ?> deepEnumAttribute) {
    super(parameterRestrictions, parameterToEnumFunction, enumAttribute);
    this.deepEnumAttribute=deepEnumAttribute;
  }

  @Override
  Path<?> getPathSingular(Root<T> root) {
    return super.getPathSingular(root).get(deepEnumAttribute.getName());
  }
}
