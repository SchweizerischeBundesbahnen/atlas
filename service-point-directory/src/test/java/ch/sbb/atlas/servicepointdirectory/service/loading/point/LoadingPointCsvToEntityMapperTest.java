package ch.sbb.atlas.servicepointdirectory.service.loading.point;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoadingPointCsvToEntityMapperTest {

  private static final String csvLine =
      """
          LADESTELLEN_NUMMER;BEZEICHNUNG;BEZEICHNUNG_LANG;IS_ANSCHLUSSPUNKT;ERSTELLT_VON;GEAENDERT_VON;GEAENDERT_AM;ERSTELLT_AM;DIDOK_CODE;GUELTIG_VON;GUELTIG_BIS;E_LV95;N_LV95;Z_LV95;E_LV03;N_LV03;Z_LV03;E_WGS84WEB;N_WGS84WEB;Z_WGS84WEB;E_WGS84;N_WGS84;Z_WGS84
          4201;Piazzale;Piazzaleee;0;fs45117;GSU_DIDOK;2018-06-28 11:48:56;2017-12-04 13:11:03;83017186;2018-06-28;2099-12-31;;;;;;;;;
          """;

  @Test
  void shouldMapAllPropertiesCorrectly() throws IOException {
    // given
    MappingIterator<LoadingPointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        LoadingPointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    List<LoadingPointVersion> loadingPoints = mappingIterator.readAll().stream().map(new LoadingPointCsvToEntityMapper())
        .toList();

    // when & then
    LoadingPointVersion expected = LoadingPointVersion.builder()
        .number(4201)
        .designation("Piazzale")
        .designationLong("Piazzaleee")
        .connectionPoint(false)
        .servicePointNumber(83017186)
        .validFrom(LocalDate.of(2018, 6, 28))
        .validTo(LocalDate.of(2099, 12, 31))
        .creator("fs45117")
        .creationDate(LocalDateTime.of(2017, 12, 4, 13, 11, 3))
        .editor("GSU_DIDOK")
        .editionDate(LocalDateTime.of(2018, 6, 28, 11, 48, 56))
        .build();

    assertThat(loadingPoints).isNotEmpty();
    assertThat(loadingPoints).first().usingRecursiveComparison().isEqualTo(expected);
  }
}
