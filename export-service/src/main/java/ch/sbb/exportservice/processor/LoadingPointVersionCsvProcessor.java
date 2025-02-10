package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.bodi.BusinessOrganisationRelation;
import ch.sbb.exportservice.entity.prm.LoadingPointVersion;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import org.springframework.batch.item.ItemProcessor;

public class LoadingPointVersionCsvProcessor implements ItemProcessor<LoadingPointVersion,
    LoadingPointVersionCsvModel> {

  @Override
  public LoadingPointVersionCsvModel process(LoadingPointVersion version) {
    final BusinessOrganisationRelation servicePointBusinessOrganisationRelation =
        version.getServicePointBusinessOrganisationRelation();
    return LoadingPointVersionCsvModel.builder()
        .number(version.getNumber())
        .designation(version.getDesignation())
        .designationLong(version.getDesignationLong())
        .connectionPoint(version.isConnectionPoint())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .servicePointNumber(version.getServicePointNumber().getNumber())
        .checkDigit(version.getServicePointNumber().getCheckDigit())
        .parentSloidServicePoint(version.getParentSloidServicePoint())
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .servicePointBusinessOrganisation(servicePointBusinessOrganisationRelation.getBusinessOrganisation())
        .servicePointBusinessOrganisationNumber(servicePointBusinessOrganisationRelation.getBusinessOrganisationNumber())
        .servicePointBusinessOrganisationAbbreviationDe(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationAbbreviationDe())
        .servicePointBusinessOrganisationAbbreviationFr(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationAbbreviationFr())
        .servicePointBusinessOrganisationAbbreviationIt(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationAbbreviationIt())
        .servicePointBusinessOrganisationAbbreviationEn(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationAbbreviationEn())
        .servicePointBusinessOrganisationDescriptionDe(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationDescriptionDe())
        .servicePointBusinessOrganisationDescriptionFr(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationDescriptionFr())
        .servicePointBusinessOrganisationDescriptionIt(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationDescriptionIt())
        .servicePointBusinessOrganisationDescriptionEn(
            servicePointBusinessOrganisationRelation.getBusinessOrganisationDescriptionEn())
        .build();
  }

}
