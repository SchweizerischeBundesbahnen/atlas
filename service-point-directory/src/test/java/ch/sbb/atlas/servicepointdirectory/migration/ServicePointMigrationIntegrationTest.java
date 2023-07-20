package ch.sbb.atlas.servicepointdirectory.migration;

import static ch.sbb.atlas.servicepointdirectory.migration.AtlasCsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
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

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePointMigrationIntegrationTest {

  static final String BASE_PATH = "/migration/";

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230720020014.csv";
  private static final String ATLAS_CSV_FILE = "full-world-service-point-2023-07-20.csv";

  private static final List<ServicePointVersionCsvModel> atlasCsvLines = new ArrayList<>();
  private static final List<ServicePointCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(ServicePointImportService.parseServicePoints(csvStream));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(AtlasCsvReader.parseAtlasServicePoints(csvStream));
    }
    assertThat(atlasCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameDidokCodesInBothCsvs() {
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

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachDidokCode() {
    Map<Integer, Validity> groupedDidokCodes = didokCsvLines.stream().collect(
        Collectors.groupingBy(ServicePointCsvModel::getDidokCode, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> new DateRange(i.getValidFrom(), i.getValidTo())).collect(Collectors.toList())).minify())));

    Map<Integer, Validity> groupedAtlasNumbers = atlasCsvLines.stream().collect(
        Collectors.groupingBy(ServicePointVersionCsvModel::getNumber, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> new DateRange(dateFromString(i.getValidFrom()), dateFromString(i.getValidTo())))
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
    Map<Integer, List<ServicePointVersionCsvModel>> groupedAtlasNumbers = atlasCsvLines.stream()
        .collect(Collectors.groupingBy(ServicePointVersionCsvModel::getNumber));

    didokCsvLines.forEach(didokCsvLine -> {
      ServicePointVersionCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getDidokCode()));
      new ServicePointMappingEquality(didokCsvLine, atlasCsvLine, true).performCheck();
    });
  }

  private ServicePointVersionCsvModel findCorrespondingAtlasServicePointVersion(ServicePointCsvModel didokCsvLine,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    List<ServicePointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
        atlasCsvLine -> new DateRange(dateFromString(atlasCsvLine.getValidFrom()),
            dateFromString(atlasCsvLine.getValidTo())).contains(
            didokCsvLine.getValidFrom())).toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }
}
