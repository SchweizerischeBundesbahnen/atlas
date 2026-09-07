package ch.sbb.line.directory.module.ttfn.search;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.BusinessOrganisationDependentSearchRestriction;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber_;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TimetableFieldNumberSearchRestrictions extends
    BusinessOrganisationDependentSearchRestriction<TimetableFieldNumber> {

  private String number;

  private List<String> ttfnIds;

  private Boolean excludeExpired;

  @Override
  protected SingularAttribute<TimetableFieldNumber, Status> getStatus() {
    return TimetableFieldNumber_.status;
  }

  @Override
  public Specification<TimetableFieldNumber> getSpecification() {
    return getBaseSpecification()
        .and(specificationBuilder().exactMatchStringSpecification(number))
        .and(specificationBuilder().stringInSpecification(
            ttfnIds == null ? List.of() : ttfnIds, TimetableFieldNumber_.ttfnid))
        .and(notExpiredSpecification());
  }

  private Specification<TimetableFieldNumber> notExpiredSpecification() {
    if (!Boolean.TRUE.equals(excludeExpired)) {
      return (root, query, criteriaBuilder) -> criteriaBuilder.and();
    }
    return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
        root.get(TimetableFieldNumber_.validTo), LocalDate.now());
  }

  @Override
  protected SpecificationBuilder<TimetableFieldNumber> specificationBuilder() {
    return SpecificationBuilder.<TimetableFieldNumber>builder()
        .stringAttributes(
            List.of(
                TimetableFieldNumber.Fields.descriptionOutwardLine1,
                TimetableFieldNumber.Fields.descriptionOutwardLine2,
                TimetableFieldNumber.Fields.descriptionOutwardLine3,
                TimetableFieldNumber.Fields.descriptionReturnLine1,
                TimetableFieldNumber.Fields.descriptionReturnLine2,
                TimetableFieldNumber.Fields.descriptionReturnLine3,
                TimetableFieldNumber.Fields.number))
        .singleStringAttribute(TimetableFieldNumber_.number)
        .validFromAttribute(TimetableFieldNumber_.validFrom)
        .validToAttribute(TimetableFieldNumber_.validTo)
        .build();
  }
}