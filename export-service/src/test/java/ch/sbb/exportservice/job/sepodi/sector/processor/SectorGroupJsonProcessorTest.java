package ch.sbb.exportservice.job.sepodi.sector.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.exportservice.job.sepodi.sectorgroup.processor.SectorGroupJsonProcessor;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SectorGroupJsonProcessorTest {

  @Test
  void shouldMapToJsonCorrectly() throws Exception {
    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .id(1L)
        .length(150.000)
        .length(120.000)
        .trafficPointSloid("ch:1:sloid:8000:1")
        .sloid("ch:1:sloid:8000:1:1")
        .designation("Designation")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .version(0)
        .build();

    ReadSectorGroupVersionModel sectorGroupVersionModel = ReadSectorGroupVersionModel.builder()
        .id(1L)
        .length(150.000)
        .length(120.000)
        .trafficPointSloid("ch:1:sloid:8000:1")
        .sloid("ch:1:sloid:8000:1:1")
        .designation("Designation")
        .creator("creator")
        .creationDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .editor("editor")
        .editionDate(LocalDateTime.of(2024, 2, 7, 20, 0))
        .etagVersion(0)
        .build();

    ReadSectorGroupVersionModel result = new SectorGroupJsonProcessor().process(sectorGroupVersion);
    assertThat(result).usingRecursiveComparison().isEqualTo(sectorGroupVersionModel);
  }

}
