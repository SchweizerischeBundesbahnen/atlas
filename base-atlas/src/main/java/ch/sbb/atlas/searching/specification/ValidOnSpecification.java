package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

public class ValidOnSpecification<T> implements Specification<T> {

  private static final long serialVersionUID = 1;

  private final Optional<LocalDate> validOn;
  private final SingularAttribute<? super T, LocalDate> validFromAttribute;
  private final SingularAttribute<? super T, LocalDate> validToAttribute;

  public ValidOnSpecification(
      Optional<LocalDate> validOn,
      SingularAttribute<? super T, LocalDate> validFromAttribute,
      SingularAttribute<? super T, LocalDate> validToAttribute) {
    this.validOn = Objects.requireNonNull(validOn);
    this.validFromAttribute = validFromAttribute;
    this.validToAttribute = validToAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    if (validOn.isEmpty()) {
      return criteriaBuilder.and();
    }
    return criteriaBuilder.and(
        criteriaBuilder.lessThanOrEqualTo(root.get(validFromAttribute), validOn.orElseThrow()),
        criteriaBuilder.greaterThanOrEqualTo(root.get(validToAttribute), validOn.orElseThrow())
    );
  }
}
