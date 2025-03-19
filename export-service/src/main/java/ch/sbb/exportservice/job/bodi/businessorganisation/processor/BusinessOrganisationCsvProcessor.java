package ch.sbb.exportservice.job.bodi.businessorganisation.processor;

import static ch.sbb.exportservice.util.MapperUtil.DATE_FORMATTER;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_FORMATTER;

import ch.sbb.atlas.api.bodi.SboidToSaidConverter;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisation;
import ch.sbb.exportservice.job.bodi.businessorganisation.model.BusinessOrganisationCsvModel;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class BusinessOrganisationCsvProcessor implements ItemProcessor<BusinessOrganisation, BusinessOrganisationCsvModel> {

  @Override
  public BusinessOrganisationCsvModel process(BusinessOrganisation businessOrganisation) {
    final List<BusinessType> sorted = businessOrganisation.getBusinessTypes().stream().sorted().toList();
    final Stream<String> businessTypesIds = sorted.stream().map(businessType -> String.valueOf(businessType.getId()));
    final Stream<String> businessTypesDe = sorted.stream().map(BusinessType::getTypeDe);
    final Stream<String> businessTypesIt = sorted.stream().map(BusinessType::getTypeIt);
    final Stream<String> businessTypesFr = sorted.stream().map(BusinessType::getTypeFr);

    return BusinessOrganisationCsvModel.builder()
        .sboid(businessOrganisation.getSboid())
        .said(SboidToSaidConverter.toSaid(businessOrganisation.getSboid()))
        .validFrom(DATE_FORMATTER.format(businessOrganisation.getValidFrom()))
        .validTo(DATE_FORMATTER.format(businessOrganisation.getValidTo()))
        .organisationNumber(businessOrganisation.getOrganisationNumber())
        .status(businessOrganisation.getStatus())
        .descriptionDe(businessOrganisation.getDescriptionDe())
        .descriptionFr(businessOrganisation.getDescriptionFr())
        .descriptionIt(businessOrganisation.getDescriptionIt())
        .descriptionEn(businessOrganisation.getDescriptionEn())
        .abbreviationDe(businessOrganisation.getAbbreviationDe())
        .abbreviationFr(businessOrganisation.getAbbreviationFr())
        .abbreviationIt(businessOrganisation.getAbbreviationIt())
        .abbreviationEn(businessOrganisation.getAbbreviationEn())
        .businessTypesId(businessTypesIds.collect(Collectors.joining(",")))
        .businessTypesDe(businessTypesDe.collect(Collectors.joining(",")))
        .businessTypesIt(businessTypesIt.collect(Collectors.joining(",")))
        .businessTypesFr(businessTypesFr.collect(Collectors.joining(",")))
        .transportCompanyNumber(businessOrganisation.getNumber())
        .transportCompanyAbbreviation(businessOrganisation.getAbbreviation())
        .transportCompanyBusinessRegisterName(businessOrganisation.getBusinessRegisterName())
        .creationTime(LOCAL_DATE_FORMATTER.format(businessOrganisation.getCreationDate()))
        .editionTime(LOCAL_DATE_FORMATTER.format(businessOrganisation.getEditionDate()))
        .build();
  }

}
