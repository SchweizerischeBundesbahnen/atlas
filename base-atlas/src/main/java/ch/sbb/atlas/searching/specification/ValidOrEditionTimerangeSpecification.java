package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ValidOrEditionTimerangeSpecification<T> implements Specification<T> {

  @Serial
  private static final long serialVersionUID = 1;

  private final LocalDate fromDate;
  private final LocalDate toDate;
  private final LocalDateTime createdAfter;
  private final LocalDateTime modifiedAfter;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    List<Predicate> predicates = new ArrayList<>();
    if (fromDate != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("validFrom"), fromDate));
    }
    if (toDate != null) {
      predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("validTo"), toDate));
    }

    if (createdAfter != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creationDate"), createdAfter));
    }
    if (modifiedAfter != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("editionDate"), modifiedAfter));
    }
    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }
}
