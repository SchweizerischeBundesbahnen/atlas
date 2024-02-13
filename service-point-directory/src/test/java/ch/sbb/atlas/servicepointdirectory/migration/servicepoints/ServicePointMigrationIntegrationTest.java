package ch.sbb.atlas.servicepointdirectory.migration.servicepoints;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.util.FileSystemUtils;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class ServicePointMigrationIntegrationTest {

  private static final String ZIPPED_DIDOK_CSV_FILE = "src/test/resources/migration/DIDOK3_DIENSTSTELLEN_ALL_V_3_20240213015645.zip";
  private static final String DECOMPRESSED_FILE_PATH = "src/test/resources/migration/DIDOK3_DIENSTSTELLEN_ALL_V_3_20240213015645";
  private static final String ATLAS_CSV_FILE = "full-world-service_point-2024-02-13.csv";

  private static final List<ServicePointAtlasCsvModel> atlasCsvLines = new ArrayList<>();
  private static final List<ServicePointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    File zippedFile = new File(ZIPPED_DIDOK_CSV_FILE);
    File unzippedFile = MigrationTestsUtilityClass.unzipFile(zippedFile, DECOMPRESSED_FILE_PATH);
    try (InputStream csvStream = new FileInputStream(unzippedFile)) {
      didokCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, ServicePointAtlasCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointDidokCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointAtlasCsvModel::getNumber).collect(Collectors.toSet());

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

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachDidokCode() {
    Map<Integer, Validity> groupedDidokCodes = didokCsvLines.stream().collect(
        Collectors.groupingBy(ServicePointDidokCsvModel::getDidokCode, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> DateRange.builder().from(i.getValidFrom()).to(i.getValidTo()).build())
                    .collect(Collectors.toList())).minify())));

    Map<Integer, Validity> groupedAtlasNumbers = atlasCsvLines.stream().collect(
        Collectors.groupingBy(ServicePointAtlasCsvModel::getNumber, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(
                        i -> DateRange.builder().from(CsvReader.dateFromString(i.getValidFrom())).to(CsvReader.dateFromString(i.getValidTo())).build())
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
  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<Integer, List<ServicePointAtlasCsvModel>> groupedAtlasNumbers = atlasCsvLines.stream()
        .collect(Collectors.groupingBy(ServicePointAtlasCsvModel::getNumber));

    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointAtlasCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getDidokCode()));
      new ServicePointMappingEquality(didokCsvLine, atlasCsvLine, true).performCheck();
    });
    FileSystemUtils.deleteRecursively(new File(DECOMPRESSED_FILE_PATH));
  }

  private ServicePointAtlasCsvModel findCorrespondingAtlasServicePointVersion(ServicePointDidokCsvModel didokCsvLine,
      List<ServicePointAtlasCsvModel> atlasCsvLines) {
    List<ServicePointAtlasCsvModel> matchedVersions = atlasCsvLines.stream().filter(
            atlasCsvLine -> DateRange.builder()
                .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(didokCsvLine.getValidFrom()))
        .toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }

}
