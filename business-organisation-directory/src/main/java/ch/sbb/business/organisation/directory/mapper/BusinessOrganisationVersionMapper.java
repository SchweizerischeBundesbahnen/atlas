package ch.sbb.business.organisation.directory.mapper;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.SboidToSaidConverter;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BusinessOrganisationVersionMapper {

  public static BusinessOrganisationVersion toEntity(BusinessOrganisationVersionModel model) {
    return BusinessOrganisationVersion.builder()
        .id(model.getId())
        .status(model.getStatus())
        .descriptionDe(model.getDescriptionDe())
        .descriptionFr(model.getDescriptionFr())
        .descriptionIt(model.getDescriptionIt())
        .descriptionEn(model.getDescriptionEn())
        .abbreviationDe(model.getAbbreviationDe())
        .abbreviationFr(model.getAbbreviationFr())
        .abbreviationIt(model.getAbbreviationIt())
        .abbreviationEn(model.getAbbreviationEn())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .organisationNumber(model.getOrganisationNumber())
        .contactEnterpriseEmail(model.getContactEnterpriseEmail())
        .sboid(model.getSboid())
        .businessTypes(model.getBusinessTypes())
        .version(model.getEtagVersion())
        .creationDate(model.getCreationDate())
        .creator(model.getCreator())
        .editionDate(model.getEditionDate())
        .editor(model.getEditor())
        .version(model.getEtagVersion())
        .build();
  }

  public static BusinessOrganisationVersionModel toModel(BusinessOrganisationVersion entity) {
    return BusinessOrganisationVersionModel
        .builder()
        .id(entity.getId())
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
        .etagVersion(entity.getVersion())
        .said(SboidToSaidConverter.toSaid(entity.getSboid()))
        .businessTypes(entity.getBusinessTypes())
        .creator(entity.getCreator())
        .creationDate(entity.getCreationDate())
        .editor(entity.getEditor())
        .editionDate(entity.getEditionDate())
        .build();
  }

  public static BusinessOrganisationVersionModel toModelFromBOExportVersionWithTuInfo(BusinessOrganisationExportVersionWithTuInfo entity) {
    return BusinessOrganisationVersionModel
            .builder()
            .id(entity.getId())
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
            .etagVersion(entity.getVersion())
            .said(SboidToSaidConverter.toSaid(entity.getSboid()))
            .businessTypes(entity.getBusinessTypes())
            .creator(entity.getCreator())
            .creationDate(entity.getCreationDate())
            .editor(entity.getEditor())
            .editionDate(entity.getEditionDate())
            .build();
  }

}
