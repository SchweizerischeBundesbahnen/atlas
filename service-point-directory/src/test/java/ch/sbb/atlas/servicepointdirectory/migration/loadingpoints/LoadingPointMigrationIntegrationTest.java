package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import static ch.sbb.atlas.servicepointdirectory.migration.CsvReader.*;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.migration.DateRange;
import ch.sbb.atlas.servicepointdirectory.migration.Validity;
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
public class LoadingPointMigrationIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_LADESTELLEN_20230906011320.csv";
  private static final String ATLAS_CSV_FILE = "full-world-loading_point-2023-09-06.csv";

  private static final List<LoadingPointAtlasCsvModel> atlasCsvLines = new ArrayList<>();
  private static final List<LoadingPointDidokCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(parseCsv(csvStream, LoadingPointDidokCsvModel.class));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(parseCsv(csvStream, LoadingPointAtlasCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameLoadingPointNumbersInBothCsvs() {
    Set<Integer> didokNumbers = didokCsvLines.stream().map(LoadingPointDidokCsvModel::getNumber).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(LoadingPointAtlasCsvModel::getNumber).collect(Collectors.toSet());

    Set<Integer> difference = atlasNumbers.stream().filter(e -> !didokNumbers.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Loading Point Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokNumbers.stream().filter(e -> !atlasNumbers.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Loading Point Numbers, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokNumbers).containsExactlyInAnyOrderElementsOf(atlasNumbers);
  }

  @Test
  @Order(3)
  void shouldHaveSameServicePointNumbersInBothCsvs() {
    Set<Integer> didokNumbers = didokCsvLines.stream().map(LoadingPointDidokCsvModel::getServicePointNumber)
        .collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(LoadingPointAtlasCsvModel::getServicePointNumber)
        .collect(Collectors.toSet());

    Set<Integer> difference = atlasNumbers.stream().filter(e -> !didokNumbers.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Service Point Numbers, which are not in Didok: {}", difference);
    }
    Set<Integer> differenceDidok = didokNumbers.stream().filter(e -> !atlasNumbers.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Service Point Numbers, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokNumbers).containsExactlyInAnyOrderElementsOf(atlasNumbers);
  }

  @Test
  @Order(4)
  void shouldHaveSameValidityOnEachLoadingPoint() {
    Map<String, Validity> groupedDidokLoadingPoints = didokCsvLines.stream().collect(
        Collectors.groupingBy(LoadingPointDidokCsvModel::getServicePointNumberAndLoadingPointNumberKey,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> new Validity(
                    list.stream().map(item -> DateRange.builder()
                        .from(item.getValidFrom())
                        .to(item.getValidTo())
                        .build()
                    ).collect(Collectors.toList())
                ).minify()
            )
        )
    );

    Map<String, Validity> groupedAtlasLoadingPoints = atlasCsvLines.stream().collect(
        Collectors.groupingBy(LoadingPointAtlasCsvModel::getServicePointNumberAndLoadingPointNumberKey,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> new Validity(
                    list.stream().map(item -> DateRange.builder()
                        .from(dateFromString(item.getValidFrom()))
                        .to(dateFromString(item.getValidTo()))
                        .build()
                    ).collect(Collectors.toList())
                ).minify()
            )
        )
    );

    List<String> validityErrors = new ArrayList<>();
    groupedDidokLoadingPoints.forEach((key, didokValidity) -> {
      Validity atlasValidity = groupedAtlasLoadingPoints.get(key);
      if (atlasValidity == null) {
        log.error("Didok Key [" + key + "] not found in ATLAS");
      } else if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on didokKey: " + key + " didokValidity=" + didokValidity + ", atlasValidity=" + atlasValidity);
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }

    assertThat(validityErrors).isEmpty();
  }

  @Test
  @Order(5)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<String, List<LoadingPointAtlasCsvModel>> groupedAtlasLoadingPoints = atlasCsvLines.stream()
        .collect(Collectors.groupingBy(LoadingPointAtlasCsvModel::getServicePointNumberAndLoadingPointNumberKey));

    didokCsvLines.forEach(didokCsvLine -> {
      LoadingPointAtlasCsvModel atlasCsvLine = findCorrespondingAtlasLoadingPointVersion(didokCsvLine,
          groupedAtlasLoadingPoints.get(didokCsvLine.getServicePointNumberAndLoadingPointNumberKey()));
      new LoadingPointMappingEquality(didokCsvLine, atlasCsvLine).performCheck();
    });
  }

  private LoadingPointAtlasCsvModel findCorrespondingAtlasLoadingPointVersion(LoadingPointDidokCsvModel didokCsvLine,
      List<LoadingPointAtlasCsvModel> atlasCsvLines) {
    List<LoadingPointAtlasCsvModel> matchedVersions = atlasCsvLines.stream().filter(
            atlasCsvLine -> DateRange.builder()
                .from(dateFromString(atlasCsvLine.getValidFrom()))
                .to(dateFromString(atlasCsvLine.getValidTo()))
                .build()
                .contains(didokCsvLine.getValidFrom()))
        .toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }

}
