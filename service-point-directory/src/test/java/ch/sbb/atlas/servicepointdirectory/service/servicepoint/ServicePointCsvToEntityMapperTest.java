package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ServicePointCsvToEntityMapperTest {

  private static final String csvLine = """
      NUMMER;LAENDERCODE;COUNTRYCODE;UIC_NUMMER;DIDOK_CODE;GUELTIG_VON;GUELTIG_BIS;STATUS;BEZEICHNUNG_OFFIZIELL;BEZEICHNUNG_LANG;ABKUERZUNG;IS_BETRIEBSPUNKT;IS_FAHRPLAN;IS_HALTESTELLE;IS_BEDIENPUNKT;IS_VERKEHRSPUNKT;IS_GRENZPUNKT;IS_VIRTUELL;ORTSCHAFTSNAME;GEMEINDENAME;BFS_NUMMER;BEZIRKSNAME;BEZIRKSNUM;KANTONSNAME;KANTONSNUM;LAND_ISO2_GEO;VERANTWORTLICHE_GO_ID;IDENTIFIKATION;ID;ERSTELLT_AM;GEAENDERT_AM;BP_ART_BEZEICHNUNG_DE;BP_ART_BEZEICHNUNG_FR;BP_ART_BEZEICHNUNG_IT;BP_ART_BEZEICHNUNG_EN;BP_BETRIEBSPUNKT_ART_ID;BPOF_ART_BEZEICHNUNG_DE;BPOF_ART_BEZEICHNUNG_FR;BPOF_ART_BEZEICHNUNG_IT;BPOF_ART_BEZEICHNUNG_EN;BPOF_BETRIEBSPUNKT_ART_ID;IS_BPS;IS_BPK;BPK_MASTER;BPTF_ART_BEZEICHNUNG_DE;BPTF_ART_BEZEICHNUNG_FR;BPTF_ART_BEZEICHNUNG_IT;BPTF_ART_BEZEICHNUNG_EN;BPTF_BETRIEBSPUNKT_ART_ID;IS_CONTAINER_HANDLING;CRDCODE;NAME;NAME_ASCII;NUTS_CODE_ID;RESPONSIBLE_IM;DESCRIPTION;BEZEICHNUNG_17;BEZEICHNUNG_35;OEFFNUNGSBEDINGUNG;RESA_BEDINGUNG;WAGENETIKETTE;ZOLL_CODE;RICHTPUNKT_CODE;BPVB_ART_BEZEICHNUNG_DE;BPVB_ART_BEZEICHNUNG_FR;BPVB_ART_BEZEICHNUNG_IT;BPVB_ART_BEZEICHNUNG_EN;BPVB_BETRIEBSPUNKT_ART_ID;BPVH_BEZEICHNUNG_ALT;BPVH_EPR_CODE;BPVH_IATA_CODE;BPVH_VERKEHRSMITTEL;BPVH_VERKEHRSMITTEL_TEXT_DE;BPVH_VERKEHRSMITTEL_TEXT_FR;BPVH_VERKEHRSMITTEL_TEXT_IT;BPVH_VERKEHRSMITTEL_TEXT_EN;MIN_GUELTIG_VON;MAX_GUELTIG_BIS;KANTONSKUERZEL;GO_NUMMER;GO_ABKUERZUNG_DE;GO_ABKUERZUNG_FR;GO_ABKUERZUNG_IT;GO_ABKUERZUNG_EN;GO_BEZEICHNUNG_DE;GO_BEZEICHNUNG_FR;GO_BEZEICHNUNG_IT;GO_BEZEICHNUNG_EN;DS_KATEGORIEN_IDS;DS_KATEGORIEN_DE;DS_KATEGORIEN_FR;DS_KATEGORIEN_IT;DS_KATEGORIEN_EN;BAV_BEMERKUNG;OST;NORD;HEIGHT;SLOID;E_LV03;N_LV03;E_LV95;N_LV95;E_WGS84;N_WGS84;E_WGS84WEB;N_WGS84WEB;IS_GEOMETRY_EMPTY;HTYP_ID;HTYP_BESCHREIBUNG_DE;HTYP_BESCHREIBUNG_FR;HTYP_BESCHREIBUNG_IT;HTYP_BESCHREIBUNG_EN;HTYP_ABKUERZUNG_DE;HTYP_ABKUERZUNG_FR;HTYP_ABKUERZUNG_IT;HTYP_ABKUERZUNG_EN;HTYP_ANHOERUNG;HTYP_IS_AKTIV;HTYP_IS_SICHTBAR;MAPPING_CRDCODE;MAPPING_EVAPLUS;MAPPING_IFOPT;MAPPING_FUTURE_ID1;MAPPING_FUTURE_ID2;MAPPING_FUTURE_ID3;MAPPING_UICCODE;LETZTER_CRD_UPDATE;TU_ABKUERZUNG;TU_AMTLICHE_BEZEICHNUNG;TU_NUMMER;TU_HR_NAME;TU_UNTERNEHMENS_ID;ERSTELLT_VON;GEAENDERT_VON;SOURCE_SPATIAL_REF
      2751;85;CH;2751;85027516;2021-04-01;2022-07-28;3;Fischbach-Göslikon, Zentrum;;;1;1;1;0;1;0;0;Fischbach-Göslikon;Fischbach-Göslikon;4067;Bremgarten;1903;Aargau;19;CH;{0cc8d629-ff44-43e2-81a2-bbed6e7f50d9};100602;{73f38d76-fe7d-4f0a-bd96-ff7ee475c43e};2022-07-29 09:44:43;2022-07-29 09:44:43;;;;;;;;;;;0;0;;;;;;;0;;;;;;;;;;;;;;;;;;;;;;~B~;~Bus~;~Bus~;~Bus~;~Bus~;1993-02-01;2099-12-31;AG;801;PAG;PAG;PAG;PAG;PostAuto AG;CarPostal SA;AutoPostale SA;PostBus Ltd;;;;;;;924871.494410176;6002817.05162414;389;ch:1:sloid:2751;665683;247031;2665683;1247031;8.30826199275;47.37084599021;924871.49441;6002817.05162;0;10;Ordentliche Haltestelle;Arrêt ordinaire;Fermata ordinaria;;Ho;Ao;Fo;;1;1;1;;;;;;;;;PAG;;#0007;PostAuto AG;CHE-112.242.941;fs45117;fs45117;LV95
      """;
  private final ServicePointCsvToEntityMapper servicePointCsvToEntityMapper = new ServicePointCsvToEntityMapper();

  @Test
  void shouldMapServicePointGeolocationCorrectly() throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    ServicePointGeolocation servicePointGeolocation = servicePointCsvToEntityMapper.mapSPGeolocationFromServicePointCsvModel(
        servicePointCsvModel);

    ServicePointGeolocation expected = ServicePointGeolocation.builder()
        .locationTypes(LocationTypes
            .builder()
            .spatialReference(SpatialReference.LV95)
            .lv03east(665683D)
            .lv03north(247031D)
            .lv95east(2665683D)
            .lv95north(1247031D)
            .wgs84east(8.30826199275)
            .wgs84north(47.37084599021)
            .wgs84webEast(924871.49441)
            .wgs84webNorth(6002817.05162)
            .height(389D)
            .build())
        .country(Country.SWITZERLAND)
        .swissCantonFsoNumber(4067)
        .swissCantonName("Aargau")
        .swissCantonNumber(19)
        .swissDistrictName("Bremgarten")
        .swissDistrictNumber(1903)
        .swissMunicipalityName("Fischbach-Göslikon")
        .swissLocalityName("Fischbach-Göslikon")
        .creationDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .editor("fs45117")
        .build();

    assertThat(servicePointGeolocation).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldMapServicePointCorrectly() throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.mapSPFromServicePointCsvModel(servicePointCsvModel);

    ServicePointVersion expected = ServicePointVersion.builder()
        .number(85027516)
        .sloid("ch:1:sloid:2751")
        .checkDigit(6)
        .numberShort(2751)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Fischbach-Göslikon, Zentrum")
        .abbreviation(null)
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100602")
        
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2022, 7, 28))
        .categories(new HashSet<>())
        .meansOfTransport(new HashSet<>(List.of(MeanOfTransport.BUS)))
        .stopPlaceType(StopPlaceType.ORDERLY)
        .operatingPointType(null)
        .creationDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .editor("fs45117")
        .build();

    assertThat(servicePointVersion).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldMapServicePointWithGeolocationCorrectly() throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(servicePointCsvModel);

    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation.builder()
        .locationTypes(LocationTypes
            .builder()
            .spatialReference(SpatialReference.LV95)
            .lv03east(665683D)
            .lv03north(247031D)
            .lv95east(2665683D)
            .lv95north(1247031D)
            .wgs84east(8.30826199275)
            .wgs84north(47.37084599021)
            .wgs84webEast(924871.49441)
            .wgs84webNorth(6002817.05162)
            .height(389D)
            .build())
        .country(Country.SWITZERLAND)
        .swissCantonFsoNumber(4067)
        .swissCantonName("Aargau")
        .swissCantonNumber(19)
        .swissDistrictName("Bremgarten")
        .swissDistrictNumber(1903)
        .swissMunicipalityName("Fischbach-Göslikon")
        .swissLocalityName("Fischbach-Göslikon")
        .creationDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion.builder()
        .number(85027516)
        .sloid("ch:1:sloid:2751")
        .checkDigit(6)
        .numberShort(2751)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Fischbach-Göslikon, Zentrum")
        .abbreviation(null)
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100602")
        
        .servicePointGeolocation(expectedServicePointGeolocation)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2022, 7, 28))
        .categories(new HashSet<>())
        .meansOfTransport(new HashSet<>(List.of(MeanOfTransport.BUS)))
        .stopPlaceType(StopPlaceType.ORDERLY)
        .operatingPointType(null)
        .creationDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .editor("fs45117")
        .build();

    assertThat(servicePointVersion).usingRecursiveComparison().ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion.getServicePointGeolocation()).usingRecursiveComparison().ignoringFields("servicePointVersion")
        .isEqualTo(expectedServicePointGeolocation);
    assertThat(servicePointVersion.getServicePointGeolocation().getServicePointVersion()).usingRecursiveComparison()
        .ignoringFields(
            "servicePointGeolocation").isEqualTo(expectedServicePoint);
  }

}
