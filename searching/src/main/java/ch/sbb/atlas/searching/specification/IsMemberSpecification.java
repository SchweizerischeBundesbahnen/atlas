package ch.sbb.atlas.searching.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.PluralAttribute;
import org.springframework.data.jpa.domain.Specification;

// <E> CollectionElementType
// <T> RootType
public class IsMemberSpecification<T, C extends Collection<E>, E> implements Specification<T> {

  private final PluralAttribute<T, C, E> pluralAttribute;
  private final Collection<E> collection;

  public IsMemberSpecification(Collection<E> collection, PluralAttribute<T, C, E> pluralAttribute) {
    this.collection = Objects.requireNonNull(collection);
    this.pluralAttribute = pluralAttribute;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    if (collection.isEmpty()) {
      return criteriaBuilder.and();
    }

    List<Predicate> predicates = new ArrayList<>();
    for (E element : collection) {
      predicates.add(criteriaBuilder.isMember(element, root.get(pluralAttribute)));
    }

    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }

}
