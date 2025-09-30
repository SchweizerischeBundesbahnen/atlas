package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class ValidOnSpecification<T> implements Specification<T> {

  @Serial private static final long serialVersionUID = 1;

  private final LocalDate validOn;
  private final transient SingularAttribute<? super T, LocalDate> validFromAttribute;
  private final transient SingularAttribute<? super T, LocalDate> validToAttribute;

  public ValidOnSpecification(
      LocalDate validOn,
      SingularAttribute<? super T, LocalDate> validFromAttribute,
      SingularAttribute<? super T, LocalDate> validToAttribute) {
    this.validOn = validOn;
    this.validFromAttribute = validFromAttribute;
    this.validToAttribute = validToAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (Objects.isNull(validOn)) {
      return criteriaBuilder.and();
    }
    return criteriaBuilder.and(
        criteriaBuilder.lessThanOrEqualTo(root.get(validFromAttribute), validOn),
        criteriaBuilder.greaterThanOrEqualTo(root.get(validToAttribute), validOn)
    );
  }
}
