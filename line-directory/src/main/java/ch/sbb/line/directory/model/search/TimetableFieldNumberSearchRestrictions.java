package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.BusinessOrganisationDependentSearchRestriction;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumber_;
import java.util.List;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class TimetableFieldNumberSearchRestrictions extends
    BusinessOrganisationDependentSearchRestriction<TimetableFieldNumber> {

  @Override
  protected SingularAttribute<TimetableFieldNumber, Status> getStatus() {
    return TimetableFieldNumber_.status;
  }

  @Override
  protected SpecificationBuilder<TimetableFieldNumber> specificationBuilder() {
    return SpecificationBuilder.<TimetableFieldNumber>builder()
        .stringAttributes(
            List.of(TimetableFieldNumber.Fields.swissTimetableFieldNumber,
                TimetableFieldNumber.Fields.description,
                TimetableFieldNumber.Fields.ttfnid,
                TimetableFieldNumber.Fields.number))
        .validFromAttribute(TimetableFieldNumber_.validFrom)
        .validToAttribute(TimetableFieldNumber_.validTo)
        .build();
  }

}
