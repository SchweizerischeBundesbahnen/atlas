package ch.sbb.business.organisation.directory.mapper;

import ch.sbb.atlas.api.bodi.BusinessOrganisationModel;
import ch.sbb.atlas.api.bodi.SboidToSaidConverter;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BusinessOrganisationMapper {

  public static BusinessOrganisationModel toModel(BusinessOrganisation entity) {
    return BusinessOrganisationModel
        .builder()
        .status(entity.getStatus())
        .descriptionDe(entity.getDescriptionDe())
        .descriptionFr(entity.getDescriptionFr())
        .descriptionIt(entity.getDescriptionIt())
        .descriptionEn(entity.getDescriptionEn())
        .abbreviationDe(entity.getAbbreviationDe())
        .abbreviationFr(entity.getAbbreviationFr())
        .abbreviationIt(entity.getAbbreviationIt())
        .abbreviationEn(entity.getAbbreviationEn())
        .validFrom(entity.getValidFrom())
        .validTo(entity.getValidTo())
        .organisationNumber(entity.getOrganisationNumber())
        .contactEnterpriseEmail(entity.getContactEnterpriseEmail())
        .sboid(entity.getSboid())
        .said(SboidToSaidConverter.toSaid(entity.getSboid()))
        .businessTypes(entity.getBusinessTypes())
        .build();
  }

}
