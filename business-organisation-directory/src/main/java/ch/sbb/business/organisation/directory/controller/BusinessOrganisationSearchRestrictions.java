package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.model.Status;
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
                                       BusinessOrganisation_.descriptionDe,
                                       BusinessOrganisation_.descriptionFr,
                                       BusinessOrganisation_.descriptionIt,
                                       BusinessOrganisation_.descriptionEn,
                                       BusinessOrganisation_.abbreviationDe,
                                       BusinessOrganisation_.abbreviationFr,
                                       BusinessOrganisation_.abbreviationIt,
                                       BusinessOrganisation_.abbreviationEn,
                                       BusinessOrganisation_.organisationNumber,
                                       BusinessOrganisation_.sboid))
                               .validFromAttribute(BusinessOrganisation_.validFrom)
                               .validToAttribute(BusinessOrganisation_.validTo)
                               .build();
  }

}
