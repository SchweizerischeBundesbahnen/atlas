package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.function.Function;

/**
 * @param <T> Type of the root of Specification
 * @param <U> Type of filter params
 * @param <E> Type of Enum to filter on
 * @param <V> Type of intermediate entity between root and enum
 */
public class EnumByConversionServicePointGeolocationSpecification<T, U, E, V> extends EnumByConversionSpecification<T, U, E> {

  private transient final SingularAttribute<T, V> enumAttribute;
  private transient final SingularAttribute<V, E> deepEnumAttribute;

  public EnumByConversionServicePointGeolocationSpecification(List<U> parameterRestrictions,
      Function<U, E> parameterToEnumFunction, SingularAttribute<T, V> enumAttribute, SingularAttribute<V, E> deepEnumAttribute) {
    super(parameterRestrictions, parameterToEnumFunction, null);
    this.enumAttribute = enumAttribute;
    this.deepEnumAttribute = deepEnumAttribute;
  }

  @Override
  Path<E> getPathSingular(Root<T> root) {
    return root.get(enumAttribute).get(deepEnumAttribute.getName());
  }
}