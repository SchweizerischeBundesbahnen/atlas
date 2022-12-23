package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference.LV95;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
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
          LADESTELLEN_NUMMER;BEZEICHNUNG;BEZEICHNUNG_LANG;IS_ANSCHLUSSPUNKT;ERSTELLT_VON;GEAENDERT_VON;GEAENDERT_AM;ERSTELLT_AM;DIDOK_CODE;GUELTIG_VON;GUELTIG_BIS;E_LV95;N_LV95;Z_LV95;E_LV03;N_LV03;Z_LV03;E_WGS84WEB;N_WGS84WEB;Z_WGS84WEB;E_WGS84;N_WGS84;Z_WGS84;SOURCE_SPATIAL_REF;
          4201;Piazzale;Piazzaleee;0;fs45117;GSU_DIDOK;2018-06-28 11:48:56;2017-12-04 13:11:03;83017186;2018-06-28;2099-12-31;2506426.604;1116455.883;-9999;506426.604;116455.883;-9999;693132.92442;5811338.77319;-9999;6.22651899935;46.19304378649;-9999;LV95;
          """;

  @Test
  void shouldMapAllPropertiesCorrectly() throws IOException {
    // given
    MappingIterator<LoadingPointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        LoadingPointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    List<LoadingPointVersion> loadingPoints = mappingIterator
        .readAll().stream().map(new LoadingPointCsvToEntityMapper()).toList();

    final LoadingPointGeolocation loadingPointGeolocation = LoadingPointGeolocation
        .builder()
        .locationTypes(LocationTypes
            .builder()
            .spatialReference(LV95)
            .lv03east(506426.604)
            .lv03north(116455.883)
            .lv95east(2506426.604)
            .lv95north(1116455.883)
            .wgs84east(6.22651899935)
            .wgs84north(46.19304378649)
            .wgs84webEast(693132.92442)
            .wgs84webNorth(5811338.77319)
            .height(-9999.0)
            .build())
        .creator("fs45117")
        .creationDate(LocalDateTime.of(2017, 12, 4, 13, 11, 3))
        .editor("GSU_DIDOK")
        .editionDate(LocalDateTime.of(2018, 6, 28, 11, 48, 56))
        .build();

    // when & then
    final LoadingPointVersion expected = LoadingPointVersion
        .builder()
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
        .loadingPointGeolocation(loadingPointGeolocation)
        .build();

    loadingPointGeolocation.setLoadingPointVersion(expected);

    assertThat(loadingPoints).isNotEmpty();
    assertThat(loadingPoints).first().usingRecursiveComparison().isEqualTo(expected);
  }
}
