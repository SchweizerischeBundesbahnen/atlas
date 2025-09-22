package ch.sbb.exportservice.job.sepodi.sector.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorVersion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SectorJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() throws Exception {
    SectorVersion sectorVersion = SectorVersion.builder()
        .id(1L)
        .length(150.000)
        .north(250.000)
        .east(250.000)
        .length(120.000)
        .trafficPointSloid("ch:1:sloid:8000:1")
        .sloid("ch:1:sloid:8000:1:1")
        .designation("Designation")
        .spatialReference(SpatialReference.LV95)
        .edgeHeight(140.000)
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    ReadSectorVersionModel sectorVersionModel = ReadSectorVersionModel.builder()
        .id(1L)
        .length(150.000)
        .sectorGeolocation(GeolocationBaseReadModel.builder()
            .spatialReference(SpatialReference.LV95)
            .lv95(CoordinatePair.builder()
                .north(250.000)
                .east(250.000)
                .spatialReference(SpatialReference.LV95)
                .build())
            .wgs84(CoordinatePair.builder()
                .north(32.12732526839)
                .east(-19.91643950156)
                .spatialReference(SpatialReference.WGS84)
                .build())
            .lv03(CoordinatePair.builder()
                .north(-999750.0)
                .east(-1999750.0)
                .spatialReference(SpatialReference.LV03)
                .build())
            .build())
        .length(120.000)
        .trafficPointSloid("ch:1:sloid:8000:1")
        .sloid("ch:1:sloid:8000:1:1")
        .designation("Designation")
        .edgeHeight(140.000)
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .etagVersion(0)
        .build();

    ReadSectorVersionModel result = new SectorJsonProcessor().process(sectorVersion);
    assertThat(result).usingRecursiveComparison().isEqualTo(sectorVersionModel);
  }

}