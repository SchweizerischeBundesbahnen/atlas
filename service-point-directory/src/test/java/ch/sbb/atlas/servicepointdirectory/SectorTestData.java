package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SectorTestData {

  public static SectorVersion getBasicSectorVersion() {

    SectorVersion sectorVersion = SectorVersion.builder()
        .sloid("ch:1:sloid:sector:1")
        .trafficPointSloid("ch:1:sloid:sector:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    return sectorVersion;
  }

  public static SectorVersion getNewBasicSectorVersion() {

    SectorVersion sectorVersion = SectorVersion.builder()
        .sloid("ch:1:sloid:sector:1111")
        .trafficPointSloid("ch:1:sloid:sector:1111")
        .validFrom(LocalDate.of(1900, 1, 1))
        .validTo(LocalDate.of(9999, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    return sectorVersion;
  }

  public static SectorVersionModel getCreateSectorVersion() {

    SectorVersionModel sectorVersionModel = SectorVersionModel.builder()
        .trafficPointSloid("ch:1:sloid:sector:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    return sectorVersionModel;
  }

  public static SectorGroupVersion getBasicSectorGroupVersion() {

    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("ch:1:sloid:group:1")
        .trafficPointSloid("ch:1:sloid:group:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .status(Status.VALIDATED)
        .build();

    return sectorGroupVersion;
  }

}
