package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
public class ServicePointMigrationIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230712021552.csv";
  private static final String ATLAS_CSV_FILE = "full-world-service-point-2023-07-12.csv";
  private static final String SEPARATOR = "/";

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

    // WIP:
    shouldHaveMappedFieldsToAtlasCorrectly(didokCsvLines, atlasCsvLines);
    log.error("Field Mapping check done.");

    // Working:
    shouldHaveSameDidokCodesInBothCsvs(didokCsvLines, atlasCsvLines);
    shouldHaveSameValidityOnEachDidokCode(didokCsvLines, atlasCsvLines);
  }

  void shouldHaveSameDidokCodesInBothCsvs(List<ServicePointCsvModel> didokCsvLines,
                                          List<ServicePointVersionCsvModel> atlasCsvLines) {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointVersionCsvModel::getNumber).collect(Collectors.toSet());

    Set<Integer> difference = atlasNumbers.stream().filter(e -> !didokCodes.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokCodes.stream().filter(e -> !atlasNumbers.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Codes, which are not in Atlas: {}", differenceDidok);
    }

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
    assertThat(validityErrors).isEmpty();
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
      ServicePointMappingEquality.performEqualityCheck(didokCsvLine, atlasCsvLine);
    });
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
