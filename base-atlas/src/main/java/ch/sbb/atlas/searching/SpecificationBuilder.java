package ch.sbb.atlas.searching;

import ch.sbb.atlas.api.servicepoint.TrafficPointElementVersionModel;
import ch.sbb.atlas.searching.specification.BooleanSpecification;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.InSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOnSpecification;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;

@Builder
public class SpecificationBuilder<T> {

  private final List<String> stringAttributes;
  private final SingularAttribute<T, LocalDate> validFromAttribute;
  private final SingularAttribute<T, LocalDate> validToAttribute;
  private final SingularAttribute<T, String> singleStringAttribute;

  public Specification<T> searchCriteriaSpecification(List<String> searchCriteria) {
    return new SearchCriteriaSpecification<>(searchCriteria, stringAttributes);
  }

  public Specification searchCriteriaSpecificationTrafficPoint(Map<String, List<String>> searchCriteria) {
    return (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      //TODO: Errorhandling & Clean Code
      for (Map.Entry<String, List<String>> entry : searchCriteria.entrySet()) {
        String key = entry.getKey();
        List<String> values = entry.getValue();
        List<Predicate> valuePredicates = new ArrayList<>();

        for (String value : values) {
          if (LocalDate.class.isAssignableFrom(root.get(key).getJavaType())){
            LocalDate date = LocalDate.parse(value);
            if (key.equals("validFrom")){
              valuePredicates.add(builder.greaterThanOrEqualTo(root.get(key), date));
            }
            if (key.equals("validTo")) {
              valuePredicates.add(builder.lessThanOrEqualTo(root.get(key), date));
            }
          }
          else if(LocalDateTime.class.isAssignableFrom(root.get(key).getJavaType())){
            LocalDateTime dateTime = LocalDateTime.parse(value);
            valuePredicates.add(builder.greaterThanOrEqualTo(root.get(key), dateTime));
          }
          else{
            valuePredicates.add(builder.equal(root.get(key), value));
          }
        }

        Predicate valuePredicate = builder.or(valuePredicates.toArray(new Predicate[0]));
        predicates.add(valuePredicate);
      }

      return builder.and(predicates.toArray(new Predicate[0]));
    };
  }
  public <V> Specification<T> enumSpecification(List<V> enumRestrictions,
      SingularAttribute<T, V> enumAttribute) {
    return new EnumSpecification<>(enumRestrictions, enumAttribute);
  }

  public Specification<T> validOnSpecification(Optional<LocalDate> validOn) {
    return new ValidOnSpecification<>(validOn, validFromAttribute, validToAttribute);
  }

  public Specification<T> singleStringSpecification(Optional<String> searchString) {
    return new SingleStringSpecification<>(searchString, singleStringAttribute.getName());
  }

  public Specification<T> stringInSpecification(List<String> searchStrings, SingularAttribute<T, String> column) {
    return inSpecification(searchStrings, column.getName());
  }

  public Specification<T> inSpecification(List<?> searchRestrictions, String columnName) {
    return new InSpecification<>(searchRestrictions, columnName);
  }

  public Specification<T> booleanSpecification(SingularAttribute<T, Boolean> attribute, Boolean value) {
    return new BooleanSpecification<>(attribute, value);
  }
}
