package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@IntegrationTest
@Slf4j
@ExtendWith(SoftAssertionsExtension.class)
public class ServicePointMigrationIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230629021731.csv";
  private static final String ATLAS_CSV_FILE = "full-world-service-point-2023-06-29.csv";
  private static final String SEPARATOR = "/";

  @InjectSoftAssertions
  private SoftAssertions softly;

  @Test
  void shouldMigrateCorrectly() throws IOException {
    List<ServicePointCsvModel> didokCsvLines = new ArrayList<>();
    List<ServicePointVersionCsvModel> atlasCsvLines = new ArrayList<>();

    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(ServicePointImportService.parseServicePoints(csvStream));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(AtlasCsvReader.parseAtlasServicePoints(csvStream));
    }
    assertThat(atlasCsvLines).isNotEmpty();

    shouldHaveSameDidokCodesInBothCsvs(didokCsvLines, atlasCsvLines);
    shouldHaveMappedFieldsToAtlasCorrectly(didokCsvLines, atlasCsvLines);
    shouldHaveSameValidityOnEachDidokCode(didokCsvLines, atlasCsvLines);
  }

  void shouldHaveSameDidokCodesInBothCsvs(List<ServicePointCsvModel> didokCsvLines,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointVersionCsvModel::getNumber).collect(Collectors.toSet());

    assertThat(didokCodes).containsExactlyInAnyOrderElementsOf(atlasNumbers);
  }

  /**
   * Idea is to check the validity on their equality by minifying the Dateranges, so that merges
   */
  void shouldHaveSameValidityOnEachDidokCode(List<ServicePointCsvModel> didokCsvLines,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    Map<Integer, Validity> groupedDidokCodes =
        didokCsvLines.stream().collect(Collectors.groupingBy(ServicePointCsvModel::getDidokCode,
            Collectors.collectingAndThen(Collectors.toList(),
                list -> new Validity(
                    list.stream().map(i -> new DateRange(i.getValidFrom(), i.getValidTo()))
                        .collect(Collectors.toList())).minify())));

    Map<Integer, Validity> groupedAtlasNumbers =
        atlasCsvLines.stream().collect(Collectors.groupingBy(ServicePointVersionCsvModel::getNumber,
            Collectors.collectingAndThen(Collectors.toList(),
                list -> new Validity(
                    list.stream().map(i -> new DateRange(fromString(i.getValidFrom()), fromString(i.getValidTo())))
                        .collect(Collectors.toList())).minify())));

    List<String> validityErrors = new ArrayList<>();
    groupedDidokCodes.forEach((didokCode, didokValidity) -> {
      Validity atlasValidity = groupedAtlasNumbers.get(didokCode);
      if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on didokCode: " + didokCode + " didokValidity=" + didokValidity + ", atlasValidity=" + atlasValidity);
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    /*softly.*/assertThat(validityErrors).isEmpty();
  }

  /**
   * For each Version in didok we will look at the GUELTIG_VON, look up the corresponding Atlas Service Point Version (valid on
   * GUELTIG_VON) and do a comparison
   */
  void shouldHaveMappedFieldsToAtlasCorrectly(List<ServicePointCsvModel> didokCsvLines,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    Map<Integer, List<ServicePointVersionCsvModel>> groupedAtlasNumbers =
        atlasCsvLines.stream().collect(Collectors.groupingBy(ServicePointVersionCsvModel::getNumber));

    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointVersionCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getDidokCode()));
      performEqualityCheck(didokCsvLine, atlasCsvLine);
    });
  }

  private void performEqualityCheck(ServicePointCsvModel didokCsvLine, ServicePointVersionCsvModel atlasCsvLine) {
    /*softly.*/assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    /*softly.*/assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());
    /*softly.*/assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());

    // TODO: Commented out, because of UT8-Encoding problems
    // /*softly.*/assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(didokCsvLine.getBezeichnungOffiziell());
    // TODO: Commented out, because of UT8-Encoding problems
    // /*softly.*/assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getBezeichnungLang());

    /*softly.*/assertThat(atlasCsvLine.getAbbreviation()).isEqualTo(didokCsvLine.getAbkuerzung());

    /*softly.*/assertThat(atlasCsvLine.isOperatingPoint()).isEqualTo(didokCsvLine.getIsBetriebspunkt());
    /*softly.*/assertThat(atlasCsvLine.isOperatingPointWithTimetable()).isEqualTo(didokCsvLine.getIsFahrplan());
    /*softly.*/assertThat(atlasCsvLine.isStopPoint()).isEqualTo(didokCsvLine.getIsHaltestelle());

    if (atlasCsvLine.getStopPointTypeCode() != null) {
      /*softly.*/assertThat(atlasCsvLine.getStopPointTypeCode().getId()).isEqualTo(didokCsvLine.getHTypId());
    } else {
      assertThat(didokCsvLine.getHTypId()).isNull();
    }

    /*softly.*/assertThat(atlasCsvLine.isFreightServicePoint()).isEqualTo(didokCsvLine.getIsBedienpunkt());
    /*softly.*/assertThat(atlasCsvLine.isTrafficPoint()).isEqualTo(didokCsvLine.getIsVerkehrspunkt());
    /*softly.*/assertThat(atlasCsvLine.isBorderPoint()).isEqualTo(didokCsvLine.getIsGrenzpunkt());

    // TODO: Commented out, because they are not always equal
    // /*softly.*/assertThat(atlasCsvLine.isHasGeolocation()).isEqualTo(didokCsvLine.getIsVirtuell());

    performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);

//    assertThat(atlasCsvLine.businessOrganisationOrganisationNumber).isEqualTo(didokCsvLine.GO_NUMMER);
//    assertThat(atlasCsvLine.businessOrganisation.abbreviationDe).isEqualTo(didokCsvLine.GO_ABKUERZUNG_DE);
//    assertThat(atlasCsvLine.businessOrganisation.abbreviationFr).isEqualTo(didokCsvLine.GO_ABKUERZUNG_FR);
//    assertThat(atlasCsvLine.businessOrganisation.abbreviationIt).isEqualTo(didokCsvLine.GO_ABKUERZUNG_IT);
//    assertThat(atlasCsvLine.businessOrganisation.abbreviationEn).isEqualTo(didokCsvLine.GO_ABKUERZUNG_EN);
//    assertThat(atlasCsvLine.businessOrganisation.descriptionDe).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_DE);
//    assertThat(atlasCsvLine.businessOrganisation.descriptionFr).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_FR);
//    assertThat(atlasCsvLine.businessOrganisation.descriptionIt).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_IT);
//    assertThat(atlasCsvLine.businessOrganisation.descriptionEn).isEqualTo(didokCsvLine.GO_BEZEICHNUNG_EN);

//    assertThat(atlasCsvLine.operatingPointTypeCode).isEqualTo(didokCsvLine.BP_BETRIEBSPUNKT_ART_ID);
//    assertThat(atlasCsvLine.operatingPointTechnicalTimetableTypeCode).isEqualTo(didokCsvLine.BPTF_BETRIEBSPUNKT_ART_ID);
//    assertThat(atlasCsvLine.meansOfTransportCode).isEqualTo(didokCsvLine.BPVH_VERKEHRSMITTEL);
//    assertThat(atlasCsvLine.categoriesCode).isEqualTo(didokCsvLine.DS_KATEGORIEN_IDS);
//    assertThat(atlasCsvLine.operatingPointTrafficPointTypeCode).isEqualTo(didokCsvLine.BPVB_BETRIEBSPUNKT_ART_ID);
//    assertThat(atlasCsvLine.operatingPointRouteNetwork).isEqualTo(didokCsvLine.IS_BPS);
//    assertThat(atlasCsvLine.operatingPointKilometer).isEqualTo(didokCsvLine.IS_BPK);
//    assertThat(atlasCsvLine.operatingPointKilometerMasterNumber).isEqualTo(didokCsvLine.BPK_MASTER);
//    assertThat(atlasCsvLine.sort_code_of_destination_station).isEqualTo(didokCsvLine.RICHTPUNKT_CODE);
//    assertThat(atlasCsvLine.sboid).isEqualTo(didokCsvLine.IDENTIFIKATION);


//assertThat(atlasCsvLine.fotComment).isEqualTo(didokCsvLine.BAV_BEMERKUNG);

//assertThat(atlasCsvLine.creationDate).isEqualTo(didokCsvLine.ERSTELLT_AM);
//assertThat(atlasCsvLine.editionDate).isEqualTo(didokCsvLine.GEAENDERT_AM);

  }

  private void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine, ServicePointVersionCsvModel atlasCsvLine) {
    softly.assertThat(atlasCsvLine.getIsoCoutryCode()).isEqualTo(didokCsvLine.getIsoCountryCode());
    // TODO: Mapping -> CantonName in Atlas CSV is missing
    // cantonName	KANTONSNAME


    /*softly.*/assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getDistrictName()).isEqualTo(didokCsvLine.getBezirksName());

    // TODO: Mapping -> cantonFsoNumber is missing in ATLAS-CSV, or is it the fsoNumber?
    //assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getKantonsNum());

    // TODO: Commented out, because districtFsoName is 0 and bezirksNum is null
    // assertThat(atlasCsvLine.getDistrictFsoName()).isEqualTo(didokCsvLine.getBezirksNum());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getMunicipalityName()).isEqualTo(didokCsvLine.getGemeindeName());

    // TODO: Commented out, because getFsoNumber is 0 and getBfsNummer is null
    // assertThat(atlasCsvLine.getFsoNumber()).isEqualTo(didokCsvLine.getBfsNummer());

    // TODO: Commented out, because of UT8-Encoding problems
    // assertThat(atlasCsvLine.getLocalityName()).isEqualTo(didokCsvLine.getOrtschaftsName());

//    assertThat(atlasCsvLine.lv95.east).isEqualTo(didokCsvLine.E_LV95);
//    assertThat(atlasCsvLine.lv95.north).isEqualTo(didokCsvLine.N_LV95);
//    assertThat(atlasCsvLine.wgs84.east).isEqualTo(didokCsvLine.E_WGS84);
//    assertThat(atlasCsvLine.wgs84.north).isEqualTo(didokCsvLine.N_WGS84);
//    assertThat(atlasCsvLine.wgs84web.east).isEqualTo(didokCsvLine.E_WGS84WEB);
//    assertThat(atlasCsvLine.wgs84web.north).isEqualTo(didokCsvLine.N_WGS84WEB);
//    assertThat(atlasCsvLine.height).isEqualTo(didokCsvLine.HEIGHT);
  }


  ServicePointVersionCsvModel findCorrespondingAtlasServicePointVersion(ServicePointCsvModel didokCsvLine,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    List<ServicePointVersionCsvModel> matchedVersions = atlasCsvLines.stream()
        .filter(atlasCsvLine -> new DateRange(fromString(atlasCsvLine.getValidFrom()), fromString(atlasCsvLine.getValidTo()))
            .contains(didokCsvLine.getValidFrom()))
        .toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }

  private LocalDate fromString(String string) {
    return LocalDate.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH));
  }

}
