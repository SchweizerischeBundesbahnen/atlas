package ch.sbb.atlas.servicepointdirectory.module.sector;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SectorTestData {

  public static SectorVersion getBasicSectorVersion() {
    return SectorVersion.builder()
        .sloid("ch:1:sloid:sector:1")
        .trafficPointSloid("ch:1:sloid:89108:123:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }

  public static SectorVersion getNewBasicSectorVersion() {
    return SectorVersion.builder()
        .sloid("ch:1:sloid:sector:1111")
        .trafficPointSloid("ch:1:sloid:89108:123:123")
        .validFrom(LocalDate.of(1900, 1, 1))
        .validTo(LocalDate.of(9999, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }

  public static CreateSectorVersionModel getCreateSectorVersion() {
    return CreateSectorVersionModel.builder()
        .trafficPointSloid("ch:1:sloid:89108:123:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .sectorGeolocation(GeolocationBaseCreateModel.builder()
            .north(1111.111)
            .east(222.222)
            .spatialReference(SpatialReference.LV95)
            .height(19.0)
            .build())
        .edgeHeight(20.0)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }

  public static SectorGroupVersion getBasicSectorGroupVersion() {
    return SectorGroupVersion.builder()
        .sloid("ch:1:sloid:group:1")
        .trafficPointSloid("ch:1:sloid:89108:123:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
  }

}
