package ch.sbb.exportservice.job.sepodi.trafficpoint;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.trafficpoint.TrafficPointVersionCsvModel.TrafficPointVersionCsvModelBuilder;
import ch.sbb.exportservice.utile.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

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
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .designation(version.getDesignation())
        .designationOperational(version.getDesignationOperational())
        .length(version.getLength())
        .boardingAreaHeight(version.getBoardingAreaHeight())
        .compassDirection(version.getCompassDirection())
        .parentSloid(version.getParentSloid())
        .trafficPointElementType(version.getTrafficPointElementType().name())
        .designationOfficial(version.getServicePointDesignationOfficial())
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
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
            servicePointSharedBusinessOrganisation.getBusinessOrganisationDescriptionEn());

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
