package ch.sbb.exportservice.job.sepodi.loadingpoint.processor;

import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.loadingpoint.entity.LoadingPointVersion;
import ch.sbb.exportservice.job.sepodi.loadingpoint.model.LoadingPointVersionCsvModel;
import ch.sbb.exportservice.util.MapperUtil;
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
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .servicePointNumber(version.getServicePointNumber().getNumber())
        .checkDigit(version.getServicePointNumber().getCheckDigit())
        .parentSloidServicePoint(version.getParentSloidServicePoint())
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
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
