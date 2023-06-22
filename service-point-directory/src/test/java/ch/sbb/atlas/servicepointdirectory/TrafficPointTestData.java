package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointTestData {

  public static TrafficPointElementVersion getBasicTrafficPoint() {
    TrafficPointElementGeolocation geolocation = getTrafficPointGeolocationBernMittelland();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .trafficPointElementGeolocation(geolocation)
        .servicePointNumber(ServicePointNumber.of(85891087))
        .sloid("ch:1:sloid:123")
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
        .east(2600783D)
        .north(1201099D)
        .height(555D)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }
}
