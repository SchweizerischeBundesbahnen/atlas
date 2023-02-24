package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity_;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ValidOrEditionTimerangeSpecification implements Specification<ServicePointVersion> {

  @Serial
  private static final long serialVersionUID = 1;

  private final LocalDate fromDate;
  private final LocalDate toDate;
  private final LocalDateTime createdAfter;
  private final LocalDateTime modifiedAfter;

  @Override
  public Predicate toPredicate(Root<ServicePointVersion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    List<Predicate> predicates = new ArrayList<>();
    if (fromDate != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ServicePointVersion_.validFrom), fromDate));
    }
    if (toDate != null) {
      predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ServicePointVersion_.validTo), toDate));
    }

    if (createdAfter != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(BaseDidokImportEntity_.creationDate), createdAfter));
    }
    if (modifiedAfter != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(BaseDidokImportEntity_.editionDate), modifiedAfter));
    }
    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }
}
