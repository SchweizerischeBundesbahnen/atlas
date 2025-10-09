package ch.sbb.business.organisation.directory.module.businessorganisation.model;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionRequestParams;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisationVersion_;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class BusinessOrganisationVersionSearchRestrictions {

  private final Pageable pageable;
  private final BusinessOrganisationVersionRequestParams businessOrganisationVersionRequestParams;

  protected SpecificationBuilder<BusinessOrganisationVersion> specificationBuilder() {
    return SpecificationBuilder.<BusinessOrganisationVersion>builder()
        .stringAttributes(
            List.of(
                BusinessOrganisationVersion.Fields.descriptionDe,
                BusinessOrganisationVersion.Fields.descriptionFr,
                BusinessOrganisationVersion.Fields.descriptionIt,
                BusinessOrganisationVersion.Fields.descriptionEn,
                BusinessOrganisationVersion.Fields.abbreviationDe,
                BusinessOrganisationVersion.Fields.abbreviationFr,
                BusinessOrganisationVersion.Fields.abbreviationIt,
                BusinessOrganisationVersion.Fields.abbreviationEn,
                BusinessOrganisationVersion.Fields.organisationNumber,
                BusinessOrganisationVersion.Fields.sboid))
        .validFromAttribute(BusinessOrganisationVersion_.validFrom)
        .validToAttribute(BusinessOrganisationVersion_.validTo)
        .build();
  }

  public Specification<BusinessOrganisationVersion> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(businessOrganisationVersionRequestParams.getSearchCriteria())
        .and(specificationBuilder().validOnSpecification(businessOrganisationVersionRequestParams.getValidOn())
            .and(new EnumSpecification<>(businessOrganisationVersionRequestParams.getStatusChoices(), "status"))
            .and(specificationBuilder().stringInSpecification(businessOrganisationVersionRequestParams.getInSboids(),
                BusinessOrganisationVersion_.sboid))
            .and(new ValidOrEditionTimerangeSpecification<>(
                businessOrganisationVersionRequestParams.getFromDate(),
                businessOrganisationVersionRequestParams.getToDate(),
                businessOrganisationVersionRequestParams.getValidToFromDate(),
                businessOrganisationVersionRequestParams.getCreatedAfter(),
                businessOrganisationVersionRequestParams.getModifiedAfter())));
  }
}
