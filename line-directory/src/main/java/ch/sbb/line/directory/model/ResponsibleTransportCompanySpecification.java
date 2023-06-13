package ch.sbb.line.directory.model;

import ch.sbb.atlas.transport.company.entity.SharedTransportCompany_;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingStatement_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class ResponsibleTransportCompanySpecification implements Specification<TimetableHearingStatement> {

  @Serial
  private static final long serialVersionUID = 1;

  private final List<Long> transportCompanyIds;

  @Override
  public Predicate toPredicate(Root<TimetableHearingStatement> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (transportCompanyIds == null || transportCompanyIds.isEmpty()) {
      return criteriaBuilder.and();
    }

    List<Predicate> predicates = new ArrayList<>();
    for (Long searchParam : transportCompanyIds) {
      predicates.add(
          criteriaBuilder.equal(root.join(TimetableHearingStatement_.responsibleTransportCompanies)
              .get(SharedTransportCompany_.id), searchParam));
    }

    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }

}
