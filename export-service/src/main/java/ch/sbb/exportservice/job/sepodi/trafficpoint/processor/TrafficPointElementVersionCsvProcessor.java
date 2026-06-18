package ch.sbb.exportservice.job.sepodi.trafficpoint.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.trafficpoint.entity.TrafficPointElementGeolocation;
import ch.sbb.exportservice.job.sepodi.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.job.sepodi.trafficpoint.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.job.sepodi.trafficpoint.model.TrafficPointVersionCsvModel.TrafficPointVersionCsvModelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class TrafficPointElementVersionCsvProcessor extends BaseSepodiProcessor implements
    ItemProcessor<TrafficPointElementVersion, TrafficPointVersionCsvModel> {

  @Override
  public TrafficPointVersionCsvModel process(TrafficPointElementVersion version) {
    SharedBusinessOrganisation servicePointSharedBusinessOrganisation = version.getServicePointSharedBusinessOrganisation();
    TrafficPointVersionCsvModelBuilder builder = TrafficPointVersionCsvModel.builder()
        .sloid(version.getSloid())
        .numberShort(version.getServicePointNumber().getNumberShort())
        .number(version.getServicePointNumber().getNumber())
        .checkDigit(version.getServicePointNumber().getCheckDigit())
        .uicCountryCode(version.getServicePointNumber().getUicCountryCode())
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .designation(version.getDesignation())
        .designationOperational(version.getDesignationOperational())
        .length(version.getLength())
        .boardingAreaHeight(version.getBoardingAreaHeight())
        .compassDirection(version.getCompassDirection())
        .parentSloid(version.getParentSloid())
        .trafficPointElementType(version.getTrafficPointElementType().name())
        .designationOfficial(version.getServicePointDesignationOfficial())
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .parentSloidServicePoint(version.getParentSloidServicePoint())
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
        .status(version.getStatus());

    if (version.getTrafficPointElementGeolocation() != null) {
      buildGeolocation(version.getTrafficPointElementGeolocation(), builder);
    }
    return builder.build();
  }

  private void buildGeolocation(TrafficPointElementGeolocation geolocation, TrafficPointVersionCsvModelBuilder builder) {
    GeolocationBaseReadModel geolocationModel = toModel(geolocation);
    builder
        .lv95East(geolocationModel.getLv95().getEast())
        .lv95North(geolocationModel.getLv95().getNorth())
        .wgs84East(geolocationModel.getWgs84().getEast())
        .wgs84North(geolocationModel.getWgs84().getNorth())
        .height(geolocationModel.getHeight());
  }

}
