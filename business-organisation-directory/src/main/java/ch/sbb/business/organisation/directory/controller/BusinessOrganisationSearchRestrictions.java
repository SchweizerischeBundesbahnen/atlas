package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation_;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class BusinessOrganisationSearchRestrictions extends
    SearchRestrictions<BusinessOrganisation> {

  @Override
  protected SingularAttribute<BusinessOrganisation, Status> getStatus() {
    return BusinessOrganisation_.status;
  }

  @Override
  protected SpecificationBuilder<BusinessOrganisation> specificationBuilder() {
    return SpecificationBuilder.<BusinessOrganisation>builder()
                               .stringAttributes(
                                   List.of(
                                       BusinessOrganisation.Fields.descriptionDe,
                                       BusinessOrganisation.Fields.descriptionFr,
                                       BusinessOrganisation.Fields.descriptionIt,
                                       BusinessOrganisation.Fields.descriptionEn,
                                       BusinessOrganisation.Fields.abbreviationDe,
                                       BusinessOrganisation.Fields.abbreviationFr,
                                       BusinessOrganisation.Fields.abbreviationIt,
                                       BusinessOrganisation.Fields.abbreviationEn,
                                       BusinessOrganisation.Fields.organisationNumber,
                                       BusinessOrganisation.Fields.sboid))
                               .validFromAttribute(BusinessOrganisation_.validFrom)
                               .validToAttribute(BusinessOrganisation_.validTo)
                               .build();
  }

}
