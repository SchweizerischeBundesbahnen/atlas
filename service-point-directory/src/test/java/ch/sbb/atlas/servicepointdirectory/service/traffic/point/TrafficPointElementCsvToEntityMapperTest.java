package ch.sbb.atlas.servicepointdirectory.service.traffic.point;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TrafficPointElementCsvToEntityMapperTest {

  private static final String csvLine =
      """
      SLOID;DS_NUMMER;DS_LAENDERCODE;GUELTIG_VON;GUELTIG_BIS;BEZEICHNUNG;BEZEICHNUNG_BETRIEBLICH;LAENGE;KANTENHOEHE;KOMPASSRICHTUNG;BPVE_ID;BPVE_TYPE;HALTEBEREICH_TYPE;E_LV95;N_LV95;Z_LV95;E_LV03;N_LV03;Z_LV03;E_WGS84;N_WGS84;Z_WGS84;DIDOK_CODE;GEAENDERT_AM;DS_SLOID;DS_BEZEICHNUNG_OFFIZIELL;DS_GO_IDENTIFIKATION;E_WGS84WEB;N_WGS84WEB;Z_WGS84WEB;ERSTELLT_AM;ERSTELLT_VON;GEAENDERT_VON;SOURCE_SPATIAL_REF
      ch:1:sloid:1400015:0:310240;15;14;2020-01-06;2099-12-31;Bezeichnung;gali00;;;277;;0;0;2505236.389;1116323.213;-9999;505236.389;116323.213;-9999;6.21113066932;46.19168377866;-9999;14000158;2019-12-06 08:02:34;;Gaillard, Libération;100680;691419.90336;5811120.06939;-9999;2019-12-06 08:02:34;fs45117;fs45117;LV95
      """;

  @Test
  void shouldMapAllPropertiesCorrectly() throws IOException {
    // given
    MappingIterator<TrafficPointElementCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        TrafficPointElementCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    List<TrafficPointElementVersion> trafficPointElementVersions = mappingIterator.readAll().stream().map(new TrafficPointElementCsvToEntityMapper())
        .toList();

    // when & then
    TrafficPointElementGeolocation trafficPointElementGeolocation = TrafficPointElementGeolocation.builder()
        .locationTypes(LocationTypes.builder()
          .spatialReference(SpatialReference.LV95)
          .lv03east(505236.389)
          .lv03north(116323.213)
          .lv95east(2505236.389)
          .lv95north(1116323.213)
          .wgs84east(6.21113066932)
          .wgs84north(46.19168377866)
          .wgs84webEast(691419.90336)
          .wgs84webNorth(5811120.06939)
          .height(-9999.0)
          .build())
        .country(Country.FRANCE_BUS)
        .creationDate(LocalDateTime.of(2019,12,6,8,2,34))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(2019,12,6,8,2,34))
        .editor("fs45117")
        .build();

    TrafficPointElementVersion expected = TrafficPointElementVersion.builder()
        .designation("Bezeichnung")
        .designationOperational("gali00")
        .servicePointNumber(14000158)
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .sloid("ch:1:sloid:1400015:0:310240")
        .compassDirection(277.0)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 6))
        .validTo(LocalDate.of(2099, 12, 31))
        .creationDate(LocalDateTime.of(2019,12,6,8,2,34))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(2019,12,6,8,2,34))
        .editor("fs45117")
        .build();
    trafficPointElementGeolocation.setTrafficPointElementVersion(expected);

    assertThat(trafficPointElementVersions).isNotEmpty();
    assertThat(trafficPointElementVersions).first().usingRecursiveComparison().isEqualTo(expected);
  }
}
