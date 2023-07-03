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
    softly.assertThat(validityErrors).isEmpty();
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
    softly.assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getDidokCode());
    softly.assertThat(atlasCsvLine.getNumberShort()).isEqualTo(didokCsvLine.getNummer());
    softly.assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());

    softly.assertThat(atlasCsvLine.getDesignationOfficial()).isEqualTo(didokCsvLine.getBezeichnungOffiziell());
    softly.assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getBezeichnungLang());
    softly.assertThat(atlasCsvLine.getAbbreviation()).isEqualTo(didokCsvLine.getAbkuerzung());

    softly.assertThat(atlasCsvLine.isOperatingPoint()).isEqualTo(didokCsvLine.getIsBetriebspunkt());
    softly.assertThat(atlasCsvLine.isOperatingPointWithTimetable()).isEqualTo(didokCsvLine.getIsFahrplan());
    softly.assertThat(atlasCsvLine.isStopPoint()).isEqualTo(didokCsvLine.getIsHaltestelle());

    if (atlasCsvLine.getStopPointTypeCode() != null) {
      softly.assertThat(atlasCsvLine.getStopPointTypeCode().getId()).isEqualTo(didokCsvLine.getHTypId());
    } else {
      assertThat(didokCsvLine.getHTypId()).isNull();
    }

    softly.assertThat(atlasCsvLine.isFreightServicePoint()).isEqualTo(didokCsvLine.getIsBedienpunkt());
    softly.assertThat(atlasCsvLine.isTrafficPoint()).isEqualTo(didokCsvLine.getIsVerkehrspunkt());
    softly.assertThat(atlasCsvLine.isBorderPoint()).isEqualTo(didokCsvLine.getIsGrenzpunkt());

    softly.assertThat(atlasCsvLine.isHasGeolocation()).isEqualTo(didokCsvLine.getIsVirtuell());

    performEqualityCheckOnGeoLocation(didokCsvLine, atlasCsvLine);
  }

  private void performEqualityCheckOnGeoLocation(ServicePointCsvModel didokCsvLine, ServicePointVersionCsvModel atlasCsvLine) {
    softly.assertThat(atlasCsvLine.getIsoCoutryCode()).isEqualTo(didokCsvLine.getIsoCountryCode());
    // Mapping -> CantonName in Atlas CSV is missing
    softly.assertThat(atlasCsvLine.getCantonAbbreviation()).isEqualTo(didokCsvLine.getKantonsKuerzel());
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
