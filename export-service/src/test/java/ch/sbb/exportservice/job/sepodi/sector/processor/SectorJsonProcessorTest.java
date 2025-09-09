package ch.sbb.exportservice.job.sepodi.sector.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
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

    SectorVersionModel sectorVersionModel = SectorVersionModel.builder()
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
        .etagVersion(0)
        .build();

    SectorVersionModel result = new SectorJsonProcessor().process(sectorVersion);
    assertThat(result).usingRecursiveComparison().isEqualTo(sectorVersionModel);
  }

}