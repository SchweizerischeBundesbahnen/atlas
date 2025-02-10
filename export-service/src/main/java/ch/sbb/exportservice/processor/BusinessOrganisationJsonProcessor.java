package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.SboidToSaidConverter;
import ch.sbb.exportservice.entity.bodi.BusinessOrganisation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class BusinessOrganisationJsonProcessor implements ItemProcessor<BusinessOrganisation, BusinessOrganisationVersionModel> {

  @Override
  public BusinessOrganisationVersionModel process(BusinessOrganisation businessOrganisation) {
    return BusinessOrganisationVersionModel.builder()
        .id(businessOrganisation.getId())
        .status(businessOrganisation.getStatus())
        .descriptionDe(businessOrganisation.getDescriptionDe())
        .descriptionFr(businessOrganisation.getDescriptionFr())
        .descriptionIt(businessOrganisation.getDescriptionIt())
        .descriptionEn(businessOrganisation.getDescriptionEn())
        .abbreviationDe(businessOrganisation.getAbbreviationDe())
        .abbreviationFr(businessOrganisation.getAbbreviationFr())
        .abbreviationIt(businessOrganisation.getAbbreviationIt())
        .abbreviationEn(businessOrganisation.getAbbreviationEn())
        .validFrom(businessOrganisation.getValidFrom())
        .validTo(businessOrganisation.getValidTo())
        .organisationNumber(businessOrganisation.getOrganisationNumber())
        .contactEnterpriseEmail(businessOrganisation.getContactEnterpriseEmail())
        .sboid(businessOrganisation.getSboid())
        .etagVersion(businessOrganisation.getVersion())
        .said(SboidToSaidConverter.toSaid(businessOrganisation.getSboid()))
        .businessTypes(businessOrganisation.getBusinessTypes())
        .creator(businessOrganisation.getCreator())
        .creationDate(businessOrganisation.getCreationDate())
        .editor(businessOrganisation.getEditor())
        .editionDate(businessOrganisation.getEditionDate())
        .build();
  }

}
