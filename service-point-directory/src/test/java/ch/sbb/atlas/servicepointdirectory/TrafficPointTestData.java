package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class TrafficPointTestData {

  public static final ServicePointNumber SERVICE_POINT_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(1400015);

  public static TrafficPointElementVersion getBasicTrafficPoint() {
    TrafficPointElementGeolocation geolocation = getTrafficPointGeolocationBernMittelland();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .trafficPointElementGeolocation(geolocation)
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
        .sloid("ch:1:sloid:123:123:123")
        .parentSloid("ch:1:sloid:123:123:123")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    geolocation.setTrafficPointElementVersion(trafficPointElementVersion);
    return trafficPointElementVersion;
  }

  public static TrafficPointElementGeolocation getTrafficPointGeolocationBernMittelland() {
    return TrafficPointElementGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600783.31256)
        .north(1201099.85634)
        .height(555.98)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }

  public static TrafficPointElementVersion getTrafficPoint() {
    TrafficPointElementGeolocation trafficPointElementGeolocation = TrafficPointElementGeolocation
            .builder()
            .spatialReference(SpatialReference.LV95)
            .east(2505236.389)
            .north(1116323.213)
            .height(-9999.0)
            .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .editor("fs45117")
            .build();

    return TrafficPointElementVersion
            .builder()
            .designation("Bezeichnung")
            .designationOperational("gali00")
            .servicePointNumber(SERVICE_POINT_NUMBER)
            .trafficPointElementGeolocation(trafficPointElementGeolocation)
            .sloid("ch:1:sloid:1400015:0:310240")
            .parentSloid("ch:1:sloid:1400015:310240")
            .compassDirection(277.0)
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .validFrom(LocalDate.of(2020, 1, 6))
            .validTo(LocalDate.of(2099, 12, 31))
            .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .editor("fs45117")
            .build();
  }

  public static ServicePointVersion testServicePointForTrafficPoint() {
    return ServicePointVersion.builder()
        .number(SERVICE_POINT_NUMBER)
        .numberShort(1)
        .country(Country.FRANCE_BUS)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
  }

  public static CreateTrafficPointElementVersionModel getCreateTrafficPointVersionModel() {
    GeolocationBaseCreateModel trafficPointElementGeolocation = GeolocationBaseCreateModel
            .builder()
            .spatialReference(SpatialReference.LV95)
            .east(2505236.389)
            .north(1116323.213)
            .height(-9999.0)
            .build();

    return CreateTrafficPointElementVersionModel
            .builder()
            .designation("Bezeichnung")
            .designationOperational("gali00")
            .numberWithoutCheckDigit(1400015)
            .trafficPointElementGeolocation(trafficPointElementGeolocation)
            .sloid("ch:1:sloid:1400015:0:310240")
            .parentSloid("ch:1:sloid:1400015:310240")
            .compassDirection(277.0)
            .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
            .validFrom(LocalDate.of(2020, 1, 6))
            .validTo(LocalDate.of(2099, 12, 31))
            .creationDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .creator("fs45117")
            .editionDate(LocalDateTime.of(2019, 12, 6, 8, 2, 34))
            .editor("fs45117")
            .build();
  }
}
