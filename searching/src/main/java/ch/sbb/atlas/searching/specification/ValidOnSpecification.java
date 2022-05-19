package ch.sbb.atlas.searching.specification;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

public class ValidOnSpecification<T> implements Specification<T> {

    private final Optional<LocalDate> validOn;
    private final SingularAttribute<T, LocalDate> validFromAttribute;
    private final SingularAttribute<T, LocalDate> validToAttribute;

    public ValidOnSpecification(
        Optional<LocalDate> validOn,
        SingularAttribute<T, LocalDate> validFromAttribute,
        SingularAttribute<T, LocalDate> validToAttribute) {
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