package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.BusinessOrganisationDependentSearchRestriction;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumber_;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Optional;
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

  @Override
  protected SingularAttribute<TimetableFieldNumber, Status> getStatus() {
    return TimetableFieldNumber_.status;
  }

  @Override
  public Specification<TimetableFieldNumber> getSpecification() {
    return getBaseSpecification().and(
        specificationBuilder().singleStringSpecification(Optional.ofNullable(number)));
  }

  @Override
  protected SpecificationBuilder<TimetableFieldNumber> specificationBuilder() {
    return SpecificationBuilder.<TimetableFieldNumber>builder()
        .stringAttributes(
            List.of(TimetableFieldNumber.Fields.swissTimetableFieldNumber,
                TimetableFieldNumber.Fields.description,
                TimetableFieldNumber.Fields.ttfnid,
                TimetableFieldNumber.Fields.number))
        .singleStringAttribute(TimetableFieldNumber_.number)
        .validFromAttribute(TimetableFieldNumber_.validFrom)
        .validToAttribute(TimetableFieldNumber_.validTo)
        .build();
  }

}
