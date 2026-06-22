package ch.sbb.exportservice.job.sepodi.loadingpoint.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.loadingpoint.entity.LoadingPointVersion;
import ch.sbb.exportservice.job.sepodi.loadingpoint.model.LoadingPointVersionCsvModel;
import org.springframework.batch.infrastructure.item.ItemProcessor;

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
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .servicePointNumber(version.getServicePointNumber().getNumber())
        .checkDigit(version.getServicePointNumber().getCheckDigit())
        .parentSloidServicePoint(version.getParentSloidServicePoint())
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
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
