package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.PluralAttribute;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ElementCollectionContainsAnySpecification<T, C extends Collection<E>, E> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final Collection<E> searchParams;
  private final PluralAttribute<T, C, E> elementCollectionAttribute;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (searchParams == null || searchParams.isEmpty()) {
      return criteriaBuilder.and();
    }

    List<Predicate> predicates = new ArrayList<>();
    for (E searchParam : searchParams) {
      predicates.add(criteriaBuilder.equal(root.join(elementCollectionAttribute.getName()), searchParam));
    }

    return criteriaBuilder.or(predicates.toArray(Predicate[]::new));
  }

}
