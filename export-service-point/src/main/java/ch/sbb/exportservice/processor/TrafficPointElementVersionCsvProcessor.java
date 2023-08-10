package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.entity.BusinessOrganisation;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.model.TrafficPointVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.format.DateTimeFormatter;

@Slf4j
public class TrafficPointElementVersionCsvProcessor extends BaseServicePointProcessor implements
        ItemProcessor<TrafficPointElementVersion, TrafficPointVersionCsvModel> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN);
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN);

    @Override
    public TrafficPointVersionCsvModel process(TrafficPointElementVersion version) {
        BusinessOrganisation servicePointBusinessOrganisation = version.getServicePointBusinessOrganisation();
        TrafficPointVersionCsvModel.TrafficPointVersionCsvModelBuilder builder = TrafficPointVersionCsvModel.builder()
                .sloid(version.getSloid())
                .numberShort(version.getServicePointNumber().getNumberShort())
                .number(version.getServicePointNumber().getNumber())
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
                .servicePointBusinessOrganisationAbbreviationDe(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationDe())
                .servicePointBusinessOrganisationAbbreviationFr(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationFr())
                .servicePointBusinessOrganisationAbbreviationIt(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationIt())
                .servicePointBusinessOrganisationAbbreviationEn(servicePointBusinessOrganisation.getBusinessOrganisationAbbreviationEn())
                .servicePointBusinessOrganisationDescriptionDe(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionDe())
                .servicePointBusinessOrganisationDescriptionFr(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionFr())
                .servicePointBusinessOrganisationDescriptionIt(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionIt())
                .servicePointBusinessOrganisationDescriptionEn(servicePointBusinessOrganisation.getBusinessOrganisationDescriptionEn());
        buildGeolocation(version, builder);
        return builder.build();

    }

    private void buildGeolocation(TrafficPointElementVersion version, TrafficPointVersionCsvModel.TrafficPointVersionCsvModelBuilder builder) {
        GeolocationBaseReadModel geolocation = toModel(version.getTrafficPointElementGeolocation());
        if(geolocation != null) {
          builder.lv95East(geolocation.getLv95().getEast())
                  .lv95North(geolocation.getLv95().getNorth())
                  .wgs84East(geolocation.getWgs84().getEast())
                  .wgs84North(geolocation.getWgs84().getNorth())
                  .wgs84WebEast(geolocation.getWgs84web().getEast())
                  .wgs84WebNorth(geolocation.getWgs84web().getNorth())
                  .height(geolocation.getHeight());
        }
    }

}
