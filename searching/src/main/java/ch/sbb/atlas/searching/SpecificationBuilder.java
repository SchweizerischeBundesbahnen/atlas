package ch.sbb.atlas.searching;

import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.SearchCriteriaSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOnSpecification;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
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
}
