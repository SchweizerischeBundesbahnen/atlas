package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.service.DidokCsvMapper;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ServicePointCsvToEntityMapperTest {

  private static final String csvHeader = """
      NUMMER;LAENDERCODE;COUNTRYCODE;UIC_NUMMER;DIDOK_CODE;GUELTIG_VON;GUELTIG_BIS;STATUS;BEZEICHNUNG_OFFIZIELL;BEZEICHNUNG_LANG;ABKUERZUNG;IS_BETRIEBSPUNKT;IS_FAHRPLAN;IS_HALTESTELLE;IS_BEDIENPUNKT;IS_VERKEHRSPUNKT;IS_GRENZPUNKT;IS_VIRTUELL;ORTSCHAFTSNAME;GEMEINDENAME;BFS_NUMMER;BEZIRKSNAME;BEZIRKSNUM;KANTONSNAME;KANTONSNUM;LAND_ISO2_GEO;VERANTWORTLICHE_GO_ID;IDENTIFIKATION;ID;ERSTELLT_AM;GEAENDERT_AM;BP_ART_BEZEICHNUNG_DE;BP_ART_BEZEICHNUNG_FR;BP_ART_BEZEICHNUNG_IT;BP_ART_BEZEICHNUNG_EN;BP_BETRIEBSPUNKT_ART_ID;BPOF_ART_BEZEICHNUNG_DE;BPOF_ART_BEZEICHNUNG_FR;BPOF_ART_BEZEICHNUNG_IT;BPOF_ART_BEZEICHNUNG_EN;BPOF_BETRIEBSPUNKT_ART_ID;IS_BPS;IS_BPK;BPK_MASTER;BPTF_ART_BEZEICHNUNG_DE;BPTF_ART_BEZEICHNUNG_FR;BPTF_ART_BEZEICHNUNG_IT;BPTF_ART_BEZEICHNUNG_EN;BPTF_BETRIEBSPUNKT_ART_ID;IS_CONTAINER_HANDLING;CRDCODE;NAME;NAME_ASCII;NUTS_CODE_ID;RESPONSIBLE_IM;DESCRIPTION;BEZEICHNUNG_17;BEZEICHNUNG_35;OEFFNUNGSBEDINGUNG;RESA_BEDINGUNG;WAGENETIKETTE;ZOLL_CODE;RICHTPUNKT_CODE;BPVB_ART_BEZEICHNUNG_DE;BPVB_ART_BEZEICHNUNG_FR;BPVB_ART_BEZEICHNUNG_IT;BPVB_ART_BEZEICHNUNG_EN;BPVB_BETRIEBSPUNKT_ART_ID;BPVH_BEZEICHNUNG_ALT;BPVH_EPR_CODE;BPVH_IATA_CODE;BPVH_VERKEHRSMITTEL;BPVH_VERKEHRSMITTEL_TEXT_DE;BPVH_VERKEHRSMITTEL_TEXT_FR;BPVH_VERKEHRSMITTEL_TEXT_IT;BPVH_VERKEHRSMITTEL_TEXT_EN;MIN_GUELTIG_VON;MAX_GUELTIG_BIS;KANTONSKUERZEL;GO_NUMMER;GO_ABKUERZUNG_DE;GO_ABKUERZUNG_FR;GO_ABKUERZUNG_IT;GO_ABKUERZUNG_EN;GO_BEZEICHNUNG_DE;GO_BEZEICHNUNG_FR;GO_BEZEICHNUNG_IT;GO_BEZEICHNUNG_EN;DS_KATEGORIEN_IDS;DS_KATEGORIEN_DE;DS_KATEGORIEN_FR;DS_KATEGORIEN_IT;DS_KATEGORIEN_EN;BAV_BEMERKUNG;OST;NORD;HEIGHT;SLOID;E_LV03;N_LV03;E_LV95;N_LV95;E_WGS84;N_WGS84;E_WGS84WEB;N_WGS84WEB;IS_GEOMETRY_EMPTY;HTYP_ID;HTYP_BESCHREIBUNG_DE;HTYP_BESCHREIBUNG_FR;HTYP_BESCHREIBUNG_IT;HTYP_BESCHREIBUNG_EN;HTYP_ABKUERZUNG_DE;HTYP_ABKUERZUNG_FR;HTYP_ABKUERZUNG_IT;HTYP_ABKUERZUNG_EN;HTYP_ANHOERUNG;HTYP_IS_AKTIV;HTYP_IS_SICHTBAR;MAPPING_CRDCODE;MAPPING_EVAPLUS;MAPPING_IFOPT;MAPPING_FUTURE_ID1;MAPPING_FUTURE_ID2;MAPPING_FUTURE_ID3;MAPPING_UICCODE;LETZTER_CRD_UPDATE;TU_ABKUERZUNG;TU_AMTLICHE_BEZEICHNUNG;TU_NUMMER;TU_HR_NAME;TU_UNTERNEHMENS_ID;ERSTELLT_VON;GEAENDERT_VON;SOURCE_SPATIAL_REF
      """;
  private static final String csvLine = csvHeader + """
      2751;85;CH;2751;85027516;2021-04-01;2022-07-28;3;Fischbach-Göslikon, Zentrum;;;1;1;1;0;1;0;0;Fischbach-Göslikon;Fischbach-Göslikon;4067;Bremgarten;1903;Aargau;19;CH;{0cc8d629-ff44-43e2-81a2-bbed6e7f50d9};100602;{73f38d76-fe7d-4f0a-bd96-ff7ee475c43e};2022-07-29 09:44:43;2022-07-29 09:44:43;;;;;;;;;;;0;0;;;;;;;0;;;;;;;;;;;;;;;;;;;;;;~B~;~Bus~;~Bus~;~Bus~;~Bus~;1993-02-01;2099-12-31;AG;801;PAG;PAG;PAG;PAG;PostAuto AG;CarPostal SA;AutoPostale SA;PostBus Ltd;;;;;;Test comment.;924871.494410176;6002817.05162414;389;ch:1:sloid:2751;665683;247031;2665683;1247031;8.30826199275;47.37084599021;924871.49441;6002817.05162;0;10;Ordentliche Haltestelle;Arrêt ordinaire;Fermata ordinaria;;Ho;Ao;Fo;;1;1;1;;;;;;;;;PAG;;#0007;PostAuto AG;CHE-112.242.941;fs45117;fs45117;LV95
      """;
  private final ServicePointCsvToEntityMapper servicePointCsvToEntityMapper =
      new ServicePointCsvToEntityMapper();

  @Test
  void shouldMapServicePointGeolocationCorrectly() throws IOException {
    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    ServicePointGeolocation servicePointGeolocation =
        servicePointCsvToEntityMapper.mapGeolocation(
            servicePointCsvModel);

    ServicePointGeolocation expected = ServicePointGeolocation
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

    ServicePointVersion servicePointVersion =
        servicePointCsvToEntityMapper.mapServicePointVersion(
            servicePointCsvModel);

    ServicePointVersion expected = ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(85027516))
        .sloid("ch:1:sloid:2751")
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
        .stopPointType(StopPointType.ORDERLY)
        .operatingPointType(OperatingPointType.STOP_POINT)
        .comment("Test comment.")
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

    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
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

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(85027516))
        .sloid("ch:1:sloid:2751")
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
        .stopPointType(StopPointType.ORDERLY)
        .operatingPointType(OperatingPointType.STOP_POINT)
        .comment("Test comment.")
        .creationDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 7, 29), LocalTime.of(9, 44, 43)))
        .editor("fs45117")
        .build();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
    assertThat(servicePointVersion
        .getServicePointGeolocation()
        .getServicePointVersion())
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation").isEqualTo(expectedServicePoint);
  }

  @Test
  void shouldImportBetriebspunktStreckengeschwindigkeitswechsel() throws IOException {
    // given
    String csvLine = csvHeader + """
        10439;85;CH;10439;85104398;2021-12-13;2099-12-31;3;UNO-Linie 712 km 0.275;;712000;1;0;0;0;0;0;1;;;;;;;;;{de7c5f30-20f9-4084-8ed3-ff9f28f419f2};100058;{704a5de1-2a39-4606-8b3b-53d58cc63838};2021-12-13 15:21:36;2021-12-13 15:21:55;;;;;;Streckengeschwindigkeitswechsel;Changement de vitesse de ligne;Variazioni di velocità di linea;Streckengeschwindigkeitswechsel;10;;;;;;;;;0;;;;;;;;;;;;;;;;;;;;;;;;;;;2021-12-13;2099-12-31;;78;SZU;SZU;SZU;SZU;Sihltal-Zürich-Uetliberg-Bahn;Sihltal-Zürich-Uetliberg-Bahn;Sihltal-Zürich-Uetliberg-Bahn;Sihltal-Zürich-Uetliberg-Bahn;;;;;;;;;;ch:1:sloid:10439;;;;;;;;;1;;;;;;;;;;;;;;;;;;;;;SZU;;#0042;Sihltal Zürich Uetliberg Bahn SZU AG;CHE-105.952.581;u150522;u150522;WGS84WEB
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(85104398))
        .sloid("ch:1:sloid:10439")
        .numberShort(10439)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("UNO-Linie 712 km 0.275")
        .abbreviation("712000")
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100058")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2021, 12, 13))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Collections.emptySet())
        .operatingPointType(OperatingPointType.ROUTE_SPEED_CHANGE)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 12, 13), LocalTime.of(15, 21, 36)))
        .creator("u150522")
        .editionDate(LocalDateTime.of(LocalDate.of(2021, 12, 13), LocalTime.of(15, 21, 55)))
        .editor("u150522")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isFalse();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isFalse();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isFalse();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
  }

  @Test
  void shouldImportTechnischerBetriebspunktSpurwechsel() throws IOException {
    // given
    String csvLine = csvHeader + """
        925;85;CH;925;85009258;2018-01-31;2099-12-31;3;Bern Ost (Spw);;BNO;1;1;0;0;0;0;0;Bern;Bern;351;Bern-Mittelland;246;Bern;2;CH;{3fabbf5d-cac0-43c9-a872-585157e8e984};100001;{2cb68b8c-60f7-4748-b072-95a161876afe};2018-01-31 13:02:54;2022-02-24 21:48:25;;;;;;;;;;;0;0;;Spurwechsel;Diagonale d'échange;Cambio binario;Spurwechsel;8;0;;;;;;;;;;;;;;;;;;;;;;;;;;;2018-01-31;2099-12-31;BE;11;SBB;CFF;FFS;SBB;Schweizerische Bundesbahnen SBB;Chemins de fer fédéraux suisses CFF;Ferrovie federali svizzere FFS;Schweizerische Bundesbahnen SBB;;;;;;;828374.789530717;5934407.10779162;533;ch:1:sloid:925;600212;200214;2600212;1200214;7.44141734415;46.95300772754;828374.78953;5934407.10779;0;;;;;;;;;;;;;;;;;;;;;SBB;;#0001;Schweizerische Bundesbahnen SBB;CHE-102.909.703;u150522;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(600212D)
        .lv03north(200214D)
        .lv95east(2600212D)
        .lv95north(1200214D)
        .wgs84east(7.44141734415)
        .wgs84north(46.95300772754)
        .wgs84webEast(828374.78953)
        .wgs84webNorth(5934407.10779)
        .height(533D)
        .country(Country.SWITZERLAND)
        .swissCantonFsoNumber(351)
        .swissCantonName("Bern")
        .swissCantonNumber(2)
        .swissDistrictName("Bern-Mittelland")
        .swissDistrictNumber(246)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 1, 31), LocalTime.of(13, 2, 54)))
        .creator("u150522")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 24), LocalTime.of(21, 48, 25)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85009258))
        .sloid("ch:1:sloid:925")
        .numberShort(925)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern Ost (Spw)")
        .abbreviation("BNO")
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2018, 1, 31))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Collections.emptySet())
        .operatingPointType(OperatingPointType.LANGE_CHANGE)
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 1, 31), LocalTime.of(13, 2, 54)))
        .creator("u150522")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 24), LocalTime.of(21, 48, 25)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isFalse();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportVerkehrspunktHaltestelle() throws IOException {
    // given
    String csvLine = csvHeader + """
        89008;85;CH;89008;85890087;2014-12-14;2021-03-31;3;Bern, Wyleregg;;;1;1;1;0;1;0;0;Bern;Bern;351;Bern-Mittelland;246;Bern;2;CH;{98aafbe4-0e11-4b43-bd9c-1ad9bf026a4e};100626;{3bf06be4-d4ea-46a9-83ca-51a556427611};2021-03-22 09:26:29;2022-02-23 17:10:10;;;;;;;;;;;0;0;;;;;;;0;;;;;;;;;;;;;;;;;;;;;;~B~;~Bus~;~Bus~;~Bus~;~Bus~;2005-08-04;2099-12-31;BE;827;SVB Auto;SVB Auto;SVB Auto;SVB Auto;Städtische Verkehrsbetriebe Bern;Städtische Verkehrsbetriebe Bern;Städtische Verkehrsbetriebe Bern;Städtische Verkehrsbetriebe Bern;;;;;;;829209.950436389;5935705.39516551;555;ch:1:sloid:89008;600783;201099;2600783;1201099;7.44891972221;46.96096808021;829209.95044;5935705.39517;0;;;;;;;;;;;;;;;;;;;;;SVB;BERNMOBIL;#0306;Städtische Verkehrsbetriebe Bern (SVB);CHE-108.954.932;fs45117;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(600783D)
        .lv03north(201099D)
        .lv95east(2600783D)
        .lv95north(1201099D)
        .wgs84east(7.44891972221)
        .wgs84north(46.96096808021)
        .wgs84webEast(829209.95044)
        .wgs84webNorth(5935705.39517)
        .height(555D)
        .country(Country.SWITZERLAND)
        .swissCantonFsoNumber(351)
        .swissCantonName("Bern")
        .swissCantonNumber(2)
        .swissDistrictName("Bern-Mittelland")
        .swissDistrictNumber(246)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85890087))
        .sloid("ch:1:sloid:89008")
        .numberShort(89008)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Wyleregg")
        .abbreviation(null)
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .operatingPointType(OperatingPointType.STOP_POINT)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isTrue();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isTrue();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportGrenzpunkt() throws IOException {
    // given
    String csvLine = csvHeader + """
        19761;85;CH;19761;85197616;2017-11-02;2099-12-31;3;Flüh Grenze;;;1;1;0;0;0;1;0;;;;;;;;FR;{c0666dd9-3c54-4425-b868-62e2d42c90cd};100019;{2af6c20c-b5c0-47e7-a179-eddc938007df};2017-11-09 11:53:05;2019-05-20 15:03:58;;;;;;;;;;;0;0;;Landesgrenze;Frontière nationale;Confine;Landesgrenze;42;0;;;;;;;;;;;;;;;;;;;;;;;;;;;2017-11-02;2099-12-31;;37;BLT-blt;BLT-blt;BLT-blt;BLT-blt;Baselland Transport;Baselland Transport;Baselland Transport;Baselland Transport;;;;;;(Tram);834747.711855612;6022399.02902105;370;ch:1:sloid:19761;604525;259900;2604525;1259900;7.49866627944;47.48984514972;834747.71186;6022399.02902;0;;;;;;;;;;;;;;;;;;;;;BLT;;#0122;BLT Baselland Transport AG;CHE-105.824.463;GSU_DIDOK;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(604525D)
        .lv03north(259900D)
        .lv95east(2604525D)
        .lv95north(1259900D)
        .wgs84east(7.49866627944)
        .wgs84north(47.48984514972)
        .wgs84webEast(834747.71186)
        .wgs84webNorth(6022399.02902)
        .height(370D)
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85197616))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100019")
        .comment("(Tram)")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2017, 11, 2))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .operatingPointType(OperatingPointType.COUNTRY_BORDER)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isFalse();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isTrue();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportTarifpunkt() throws IOException {
    // given
    String csvLine = csvHeader + """
        94805;85;CH;94805;85948059;2020-12-13;2099-12-31;3;Dettighofen (D), Eichberg;;;1;1;0;0;1;0;0;;;;;;;;DE;{f1b7ca21-0ef8-4330-a93f-3006e6ebdeff};100311;{b2c7799d-b7ae-48f3-88dc-27cfb63103f6};2017-11-09 11:53:05;2020-08-13 14:20:18;;;;;;;;;;;0;0;;;;;;;0;;;;;;;;;;;;;;Tarifpunkt;Point tarifaire;Punto tariffale;Tarifpunkt;50;;;;;;;;;2011-09-02;2099-12-31;;354;SBG;SBG;SBG;SBG;Südbadenbus GmbH;Südbadenbus GmbH;Südbadenbus GmbH;Südbadenbus GmbH;;;;;;Fahrplan unter 11 03924-8 Dettighofen (D), Eichberg;942294.18528127;6045035.27356491;554;ch:1:sloid:94805;677131;275660;2677131;1275660;8.46477268775;47.62706979146;942294.18528;6045035.27356;0;;;;;;;;;;;;;;;;;;;;;SBG;;#0053;SBG SüdbadenBus GmbH;;GSU_DIDOK;u150522;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(677131D)
        .lv03north(275660D)
        .lv95east(2677131D)
        .lv95north(1275660D)
        .wgs84east(8.46477268775)
        .wgs84north(47.62706979146)
        .wgs84webEast(942294.18528)
        .wgs84webNorth(6045035.27356)
        .height(554D)
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2020, 8, 13), LocalTime.of(14, 20, 18)))
        .editor("u150522")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85948059))
        .sloid("ch:1:sloid:94805")
        .numberShort(94805)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Dettighofen (D), Eichberg")
        .abbreviation(null)
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100311")
        .comment("Fahrplan unter 11 03924-8 Dettighofen (D), Eichberg")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 12, 13))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .operatingPointType(OperatingPointType.TARIFF_POINT)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2020, 8, 13), LocalTime.of(14, 20, 18)))
        .editor("u150522")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isTrue();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();
    // IS_BPS
    assertThat(expectedServicePoint.isOperatingPointRouteNetwork()).isFalse();
    // IS_BPK
    assertThat(expectedServicePoint.isOperatingPointKilometer()).isFalse();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportBedienpunkt() throws IOException {
    // given
    String csvLine = csvHeader + """
        3507;85;CH;3507;85035071;2018-02-18;2019-12-05;3;Zürich RB Limmattal;;RBL;1;1;0;1;1;0;0;Spreitenbach;Spreitenbach;4040;Baden;1902;Aargau;19;CH;{3fabbf5d-cac0-43c9-a872-585157e8e984};100001;{796d9a76-0c14-4724-bc5a-db5b4ba53405};2019-12-10 13:35:07;2019-12-10 13:35:07;;;;;;;;;;;1;1;85035071;;;;;;0;3507;Zürich RB Limmattal;Zuerich RB Limmattal;CH033;85;;ZUERICH RBL;ZUERICH RB LIMMATTAL;7;0;35071;0;35071;;;;;;;;;;;;;;1993-01-01;2099-12-31;AG;11;SBB;CFF;FFS;SBB;Schweizerische Bundesbahnen SBB;Chemins de fer fédéraux suisses CFF;Ferrovie federali svizzere FFS;Schweizerische Bundesbahnen SBB;;;;;;Güterverkehr;932556.770286969;6011367.51130927;395.1;ch:1:sloid:3507;670828.009;252871.497;2670828.009;1252871.497;8.37730000058;47.42284000105;932556.77029;6011367.51131;0;;;;;;;;;;;;;CH:3507;;;;;;85035071;;SBB;;#0001;Schweizerische Bundesbahnen SBB;CHE-102.909.703;fs45117;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(670828.009)
        .lv03north(252871.497)
        .lv95east(2670828.009)
        .lv95north(1252871.497)
        .wgs84east(8.37730000058)
        .wgs84north(47.42284000105)
        .wgs84webEast(932556.77029)
        .wgs84webNorth(6011367.51131)
        .height(395.1)
        .swissCantonFsoNumber(4040)
        .swissCantonName("Aargau")
        .swissCantonNumber(19)
        .swissDistrictName("Baden")
        .swissDistrictNumber(1902)
        .swissMunicipalityName("Spreitenbach")
        .swissLocalityName("Spreitenbach")
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 35, 7)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 35, 7)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85035071))
        .sloid("ch:1:sloid:3507")
        .numberShort(3507)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Zürich RB Limmattal")
        .abbreviation("RBL")
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .comment("Güterverkehr")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2018, 2, 18))
        .validTo(LocalDate.of(2019, 12, 5))
        .categories(new HashSet<>())
        .operatingPointType(OperatingPointType.FREIGHT_POINT)
        .sortCodeOfDestinationStation("35071")
        .operatingPointKilometerMaster(ServicePointNumber.of(85035071))
        .operatingPointRouteNetwork(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 35, 7)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 35, 7)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isTrue();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isTrue();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();
    // IS_BPS
    assertThat(expectedServicePoint.isOperatingPointRouteNetwork()).isTrue();
    // IS_BPK
    assertThat(expectedServicePoint.isOperatingPointKilometer()).isTrue();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportBedienpunktArthGoldau() throws IOException {
    // given
    String csvLine = csvHeader + """
        15396;85;CH;15396;85153965;2019-07-16;2019-12-05;3;Arth-Goldau Nordwest;;GDNW;1;1;0;0;0;0;0;Goldau;Arth;1362;Schwyz;506;Schwyz;5;CH;{3fabbf5d-cac0-43c9-a872-585157e8e984};100001;{e1bc0adb-e7a0-42f2-99e9-424b95592564};2019-12-10 13:33:34;2019-12-10 13:33:34;;;;;;;;;;;0;1;85050047;Zugeordneter Betriebspunkt;Point d’exploitation associé;Punto d’esercizio associato;Zugeordneter Betriebspunkt;16;0;15396;Arth-Goldau Nordwest;Arth-Goldau Nordwest;CH063;85;;;;;;;;;;;;;;;;;;;;;;1993-01-01;2099-12-31;SZ;11;SBB;CFF;FFS;SBB;Schweizerische Bundesbahnen SBB;Chemins de fer fédéraux suisses CFF;Ferrovie federali svizzere FFS;Schweizerische Bundesbahnen SBB;;;;;;(Zug);951731.552195767;5950116.63331048;509.6;ch:1:sloid:15396;684412.363;211510.433;2684412.363;1211510.433;8.54954999716;47.0492499942;951731.5522;5950116.63331;0;;;;;;;;;;;;;CH:15396;;;;;;;;SBB;;#0001;Schweizerische Bundesbahnen SBB;CHE-102.909.703;fs45117;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(684412.363)
        .lv03north(211510.433)
        .lv95east(2684412.363)
        .lv95north(1211510.433)
        .wgs84east(8.54954999716)
        .wgs84north(47.0492499942)
        .wgs84webEast(951731.5522)
        .wgs84webNorth(5950116.63331)
        .height(509.6)
        .swissCantonFsoNumber(1362)
        .swissCantonName("Schwyz")
        .swissCantonNumber(5)
        .swissDistrictName("Schwyz")
        .swissDistrictNumber(506)
        .swissMunicipalityName("Arth")
        .swissLocalityName("Goldau")
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 33, 34)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 33, 34)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85153965))
        .sloid("ch:1:sloid:15396")
        .numberShort(15396)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Arth-Goldau Nordwest")
        .abbreviation("GDNW")
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .comment("(Zug)")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2019, 7, 16))
        .validTo(LocalDate.of(2019, 12, 5))
        .categories(new HashSet<>())
        .operatingPointType(OperatingPointType.ASSIGNED_OPERATING_POINT)
        .operatingPointKilometerMaster(ServicePointNumber.of(85050047))
        .operatingPointRouteNetwork(false)
        .creationDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 33, 34)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 12, 10), LocalTime.of(13, 33, 34)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isFalse();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();
    // IS_BPS
    assertThat(expectedServicePoint.isOperatingPointRouteNetwork()).isFalse();
    // IS_BPK
    assertThat(expectedServicePoint.isOperatingPointKilometer()).isTrue();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }

  @Test
  void shouldImportUnknownBetriebspunktWithCategories() throws IOException {
    // given
    String csvLine = csvHeader + """
        17349;85;CH;17349;85173492;2006-08-31;2006-12-04;3;Zürich Langstr Dienstgebäude;;ZLDG;1;1;0;0;0;0;0;Zürich;Zürich;261;Zürich;112;Zürich;1;CH;{3fabbf5d-cac0-43c9-a872-585157e8e984};100001;{f44c58ed-5238-4c1a-a493-07c6e4f026a8};2017-11-10 14:45:12;2018-03-06 08:15:07;;;;;;;;;;;0;0;;Nicht spezifiziert;Non spécifié;Non spezificato;Nicht spezifiziert;37;0;;;;;;;;;;;;;;;;;;;;;;;;;;;2006-08-31;2099-12-31;ZH;11;SBB;CFF;FFS;SBB;Schweizerische Bundesbahnen SBB;Chemins de fer fédéraux suisses CFF;Ferrovie federali svizzere FFS;Schweizerische Bundesbahnen SBB;1|16;Unterhaltstelle|Migr. (alt Uhst Mobile Equipe);Point d'entretien|Migr. (alt Uhst Mobile Equipe);Punto di manutenzione|Migr. (alt Uhst Mobile Equipe);Unterhaltstelle|Migr. (alt Uhst Mobile Equipe);;949301.56711918;6004267.83043376;408;ch:1:sloid:17349;682244;248219;2682244;1248219;8.52772106982;47.37967156382;949301.56712;6004267.83043;0;;;;;;;;;;;;;;;;;;;;;SBB;;#0001;Schweizerische Bundesbahnen SBB;CHE-102.909.703;fs45117;fs45117;LV95
        """;

    MappingIterator<ServicePointCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(
        ServicePointCsvModel.class).with(DidokCsvMapper.CSV_SCHEMA).readValues(csvLine);
    ServicePointCsvModel servicePointCsvModel = mappingIterator.next();

    // when
    ServicePointVersion servicePointVersion = servicePointCsvToEntityMapper.apply(
        servicePointCsvModel);

    // then
    ServicePointGeolocation expectedServicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .lv03east(682244.0)
        .lv03north(248219.0)
        .lv95east(2682244.0)
        .lv95north(1248219.0)
        .wgs84east(8.52772106982)
        .wgs84north(47.37967156382)
        .wgs84webEast(949301.56712)
        .wgs84webNorth(6004267.83043)
        .height(408.0)
        .swissCantonFsoNumber(261)
        .swissCantonName("Zürich")
        .swissCantonNumber(1)
        .swissDistrictName("Zürich")
        .swissDistrictNumber(112)
        .swissMunicipalityName("Zürich")
        .swissLocalityName("Zürich")
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 10), LocalTime.of(14, 45, 12)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 3, 6), LocalTime.of(8, 15, 7)))
        .editor("fs45117")
        .build();

    ServicePointVersion expectedServicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(expectedServicePointGeolocation)
        .number(ServicePointNumber.of(85173492))
        .sloid("ch:1:sloid:17349")
        .numberShort(17349)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Zürich Langstr Dienstgebäude")
        .abbreviation("ZLDG")
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .comment(null)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2006, 8, 31))
        .validTo(LocalDate.of(2006, 12, 4))
        .categories(Set.of(Category.MAINTENANCE_POINT, Category.MIGRATION_MOBILE_EQUIPE))
        .operatingPointType(OperatingPointType.UNKNOWN)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 10), LocalTime.of(14, 45, 12)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 3, 6), LocalTime.of(8, 15, 7)))
        .editor("fs45117")
        .build();

    // IS_BETRIEBSPUNKT
    assertThat(expectedServicePoint.isOperatingPoint()).isTrue();
    // IS_FAHRPLAN
    assertThat(expectedServicePoint.getOperatingPointType().hasTimetable()).isTrue();
    // IS_HALTESTELLE
    assertThat(expectedServicePoint.isStopPlace()).isFalse();
    // IS_BEDIENPUNKT
    assertThat(expectedServicePoint.isFreightServicePoint()).isFalse();
    // IS_VERKEHRSPUNKT
    assertThat(expectedServicePoint.isTrafficPoint()).isFalse();
    // IS_GRENZPUNKT
    assertThat(expectedServicePoint.isBorderPoint()).isFalse();
    // IS_VIRTUELL
    assertThat(expectedServicePoint.hasGeolocation()).isTrue();
    // IS_BPS
    assertThat(expectedServicePoint.isOperatingPointRouteNetwork()).isFalse();
    // IS_BPK
    assertThat(expectedServicePoint.isOperatingPointKilometer()).isFalse();

    assertThat(servicePointVersion)
        .usingRecursiveComparison()
        .ignoringFields("servicePointGeolocation")
        .isEqualTo(expectedServicePoint);
    assertThat(servicePointVersion
        .getServicePointGeolocation())
        .usingRecursiveComparison()
        .ignoringFields("servicePointVersion").isEqualTo(expectedServicePointGeolocation);
  }
}
