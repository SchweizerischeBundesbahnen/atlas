package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.entity.BusinessOrganisation;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel.TrafficPointVersionCsvModelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TrafficPointElementVersionCsvProcessor extends BaseServicePointProcessor implements
    ItemProcessor<TrafficPointElementVersion, TrafficPointVersionCsvModel> {

  @Override
  public TrafficPointVersionCsvModel process(TrafficPointElementVersion version) {
    BusinessOrganisation servicePointBusinessOrganisation = version.getServicePointBusinessOrganisation();
    TrafficPointVersionCsvModelBuilder builder = TrafficPointVersionCsvModel.builder()
        .sloid(version.getSloid())
        .numberShort(version.getServicePointNumber().getNumberShort())
        .number(version.getServicePointNumber().getNumber())
        .checkDigit(version.getServicePointNumber().getCheckDigit())
        .uicCountryCode(version.getServicePointNumber().getUicCountryCode())
        .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER.format(version.getValidTo()))
        .designation(version.getDesignation())
        .designationOperational(version.getDesignationOperational())
        .length(version.getLength())
        .boardingAreaHeight(version.getBoardingAreaHeight())
        .compassDirection(version.getCompassDirection())
        .parentSloid(version.getParentSloid())
        .trafficPointElementType(version.getTrafficPointElementType().name())
        .designationOfficial(version.getServicePointDesignationOfficial())
        .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .parentSloidServicePoint(version.getParentSloidServicePoint())
        .servicePointBusinessOrganisation(servicePointBusinessOrganisation.getBusinessOrganisation())
        .servicePointBusinessOrganisationNumber(servicePointBusinessOrganisation.getBusinessOrganisationNumber())
        .servicePointBusinessOrganisationAbbreviationDe(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationDe())
        .servicePointBusinessOrganisationAbbreviationFr(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationFr())
        .servicePointBusinessOrganisationAbbreviationIt(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationIt())
        .servicePointBusinessOrganisationAbbreviationEn(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationEn())
        .servicePointBusinessOrganisationDescriptionDe(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionDe())
        .servicePointBusinessOrganisationDescriptionFr(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionFr())
        .servicePointBusinessOrganisationDescriptionIt(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionIt())
        .servicePointBusinessOrganisationDescriptionEn(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionEn());

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
