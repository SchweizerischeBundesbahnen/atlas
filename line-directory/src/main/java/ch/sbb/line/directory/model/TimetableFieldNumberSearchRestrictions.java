package ch.sbb.line.directory.model;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumber_;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class TimetableFieldNumberSearchRestrictions extends
    SearchRestrictions<TimetableFieldNumber> {

  @Override
  protected SingularAttribute<TimetableFieldNumber, Status> getStatus() {
    return TimetableFieldNumber_.status;
  }

  @Override
  protected SpecificationBuilder<TimetableFieldNumber> specificationBuilder() {
    return SpecificationBuilder.<TimetableFieldNumber>builder()
                               .stringAttributes(
                                   List.of(TimetableFieldNumber_.swissTimetableFieldNumber,
                                       TimetableFieldNumber_.description,
                                       TimetableFieldNumber_.ttfnid, TimetableFieldNumber_.number,
                                       TimetableFieldNumber_.businessOrganisation))
                               .validFromAttribute(TimetableFieldNumber_.validFrom)
                               .validToAttribute(TimetableFieldNumber_.validTo)
                               .build();
  }

}
