package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.bodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.entity.sepodi.LoadingPointVersion;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import org.springframework.batch.item.ItemProcessor;

public class LoadingPointVersionCsvProcessor implements ItemProcessor<LoadingPointVersion,
    LoadingPointVersionCsvModel> {

  @Override
  public LoadingPointVersionCsvModel process(LoadingPointVersion version) {
    final SharedBusinessOrganisation servicePointSharedBusinessOrganisation =
        version.getServicePointSharedBusinessOrganisation();
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
        .servicePointBusinessOrganisation(servicePointSharedBusinessOrganisation.getBusinessOrganisation())
        .servicePointBusinessOrganisationNumber(servicePointSharedBusinessOrganisation.getBusinessOrganisationNumber())
        .servicePointBusinessOrganisationAbbreviationDe(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationAbbreviationDe())
        .servicePointBusinessOrganisationAbbreviationFr(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationAbbreviationFr())
        .servicePointBusinessOrganisationAbbreviationIt(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationAbbreviationIt())
        .servicePointBusinessOrganisationAbbreviationEn(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationAbbreviationEn())
        .servicePointBusinessOrganisationDescriptionDe(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationDescriptionDe())
        .servicePointBusinessOrganisationDescriptionFr(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationDescriptionFr())
        .servicePointBusinessOrganisationDescriptionIt(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationDescriptionIt())
        .servicePointBusinessOrganisationDescriptionEn(
            servicePointSharedBusinessOrganisation.getBusinessOrganisationDescriptionEn())
        .build();
  }

}
