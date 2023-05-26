package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionRequestParams;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion_;
import java.util.List;
import java.util.Optional;
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
        .and(specificationBuilder().validOnSpecification(
                Optional.ofNullable(businessOrganisationVersionRequestParams.getValidOn()))
            .and(new EnumSpecification<>(businessOrganisationVersionRequestParams.getStatusChoices(), BaseVersion.Fields.status))
            .and(specificationBuilder().stringInSpecification(businessOrganisationVersionRequestParams.getInSboids(),
                BusinessOrganisationVersion_.sboid))
            .and(new ValidOrEditionTimerangeSpecification<>(
                businessOrganisationVersionRequestParams.getFromDate(),
                businessOrganisationVersionRequestParams.getToDate(),
                businessOrganisationVersionRequestParams.getCreatedAfter(),
                businessOrganisationVersionRequestParams.getModifiedAfter())));
  }
}
