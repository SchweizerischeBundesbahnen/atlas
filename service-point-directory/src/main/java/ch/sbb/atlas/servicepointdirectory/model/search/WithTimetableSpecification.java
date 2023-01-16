package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTypes;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class WithTimetableSpecification implements Specification<ServicePointVersion> {

  private final Boolean withTimetable;

  @Override
  public Predicate toPredicate(Root<ServicePointVersion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (withTimetable == null) {
      return criteriaBuilder.and();
    }
    Predicate isWithTimetablePredicate = getIsWithTimetablePredicate(root, criteriaBuilder);
    return withTimetable ? isWithTimetablePredicate : isWithTimetablePredicate.not();
  }

  private Predicate getIsWithTimetablePredicate(Root<ServicePointVersion> root, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.or(
        root.get(ServicePointVersion_.operatingPointType).isNull(),
        root.get(ServicePointVersion_.operatingPointType).in(OperatingPointTypes.TYPES_WITH_TIMETABLE)
    );
  }
}
