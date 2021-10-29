package ch.sbb.line.directory.swiss.number;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwissNumberUniqueValidator {

  private final EntityManager entityManager;

  public boolean hasUniqueBusinessIdOverTime(SwissNumber swissNumber) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> query = cb.createQuery(swissNumber.getClass());
    Root<?> root = query.from(swissNumber.getClass());

    List<Predicate> predicates = new ArrayList<>();
    if (swissNumber.getId() != null) {
      predicates.add(cb.notEqual(root.get("id"), swissNumber.getId()));
    }
    predicates.add(
        cb.equal(root.get(swissNumber.getSwissNumberDescriptor().getName()),
            swissNumber.getSwissNumberDescriptor().getValue()));
    predicates.add(cb.and(
        cb.greaterThanOrEqualTo(root.get("validTo"), swissNumber.getValidFrom()),
        cb.lessThanOrEqualTo(root.get("validFrom"), swissNumber.getValidTo())
    ));
    query.where(predicates.toArray(new Predicate[]{}));

    List<?> conflicts = entityManager.createQuery(query).getResultList();
    return conflicts.isEmpty();
  }

}
