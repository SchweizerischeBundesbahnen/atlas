package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.BusinessOrganisation;
import ch.sbb.exportservice.entity.LoadingPointVersion;
import ch.sbb.exportservice.model.LoadingPointVersionCsvModel;
import org.springframework.batch.item.ItemProcessor;

public class LoadingPointVersionCsvProcessor implements ItemProcessor<LoadingPointVersion,
    LoadingPointVersionCsvModel> {

  @Override
  public LoadingPointVersionCsvModel process(LoadingPointVersion version) {
    final BusinessOrganisation servicePointBusinessOrganisation = version.getServicePointBusinessOrganisation();
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
        .servicePointBusinessOrganisation(servicePointBusinessOrganisation.getBusinessOrganisation())
        .servicePointBusinessOrganisationNumber(servicePointBusinessOrganisation.getBusinessOrganisationNumber())
        .servicePointBusinessOrganisationAbbreviationDe(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationDe())
        .servicePointBusinessOrganisationAbbreviationFr(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationFr())
        .servicePointBusinessOrganisationAbbreviationIt(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationIt())
        .servicePointBusinessOrganisationAbbreviationEn(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationEn())
        .servicePointBusinessOrganisationDescriptionDe(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionDe())
        .servicePointBusinessOrganisationDescriptionFr(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionFr())
        .servicePointBusinessOrganisationDescriptionIt(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionIt())
        .servicePointBusinessOrganisationDescriptionEn(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionEn())
        .build();
  }

}
