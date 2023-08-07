package ch.sbb.atlas.servicepointdirectory.migration;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrafficPointMigrationIntegrationTest {

  static final String BASE_PATH = "/migration/";

  private static final String DIDOK_CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20230807011916.csv";
  private static final String ATLAS_CSV_FILE = "full-world-traffic_point-2023-08-07.csv";

  private static final List<TrafficPointVersionCsvModel> trafficPointElementCsvModels = new ArrayList<>();
  private static final List<TrafficPointElementCsvModel> didokCsvLines = new ArrayList<>();

  @Test
  @Order(1)
  void shouldParseCsvsCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(TrafficPointElementImportService.parseTrafficPointElements(csvStream));
    }
    assertThat(didokCsvLines).isNotEmpty();

    try (InputStream csvStream = this.getClass().getResourceAsStream(BASE_PATH + ATLAS_CSV_FILE)) {
      trafficPointElementCsvModels.addAll(AtlasCsvReader.parseAtlasTraffics(csvStream));
    }
    assertThat(trafficPointElementCsvModels).isNotEmpty();
  }

  @Test
  @Order(2)
  void shouldHaveSameSloidInBothCsvs() {
    Set<String> didokSloids = didokCsvLines.stream().map(TrafficPointElementCsvModel::getSloid).collect(Collectors.toSet());
    Set<String> atlasSloids = trafficPointElementCsvModels.stream().map(TrafficPointVersionCsvModel::getSloid).collect(Collectors.toSet());

    Set<String> difference = atlasSloids.stream().filter(e -> !didokSloids.contains(e)).collect(Collectors.toSet());
    if (!difference.isEmpty()) {
      log.error("We have Atlas Sloids, which are not in Didok: {}", difference);
    }
    Set<String> differenceDidok = didokSloids.stream().filter(e -> !atlasSloids.contains(e)).collect(Collectors.toSet());
    if (!differenceDidok.isEmpty()) {
      log.error("We have Didok Sloids, which are not in Atlas: {}", differenceDidok);
    }

    assertThat(didokSloids).containsExactlyInAnyOrderElementsOf(atlasSloids);
  }

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachDidokCode() {
    //TODO: regenerate Export after this Traffic Point is fixed on Didok
    List<TrafficPointElementCsvModel> filterOverlappedVersionToBeFixed = didokCsvLines.stream().filter(trafficPointElementCsvModel -> !trafficPointElementCsvModel.getSloid().equals("ch:1:sloid:70577:0:17162")).toList();
    Map<String, Validity> groupedDidokSloids = filterOverlappedVersionToBeFixed.stream().collect(
        Collectors.groupingBy(TrafficPointElementCsvModel::getSloid, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> new DateRange(i.getValidFrom(), i.getValidTo())).collect(Collectors.toList())).minify())));

    Map<String, Validity> groupedAtlasSloids = trafficPointElementCsvModels.stream().collect(
        Collectors.groupingBy(TrafficPointVersionCsvModel::getSloid, Collectors.collectingAndThen(Collectors.toList(),
            list -> new Validity(
                list.stream().map(i -> new DateRange(AtlasCsvReader.dateFromString(i.getValidFrom()), AtlasCsvReader.dateFromString(i.getValidTo())))
                    .collect(Collectors.toList())).minify())));

    List<String> validityErrors = new ArrayList<>();
    groupedDidokSloids.forEach((sloid, didokValidity) -> {
      Validity atlasValidity = groupedAtlasSloids.get(sloid);
      if(atlasValidity == null){
        System.out.println("Didok SLOID ["+sloid+"] not found in ATLAS");
      } else  if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on didokCode: " + sloid + " didokValidity=" + didokValidity + ", atlasValidity=" + atlasValidity);
      }
    });

    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    assertThat(validityErrors).isEmpty();
  }

  /**
   * For each Version in didok we will look at the GUELTIG_VON, look up the corresponding Atlas Traffic Point Version (valid on
   * GUELTIG_VON) and do a comparison
   */
  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    Map<String, List<TrafficPointVersionCsvModel>> groupedAtlasNumbers = trafficPointElementCsvModels.stream()
        .collect(Collectors.groupingBy(TrafficPointVersionCsvModel::getSloid));

    didokCsvLines.forEach(didokCsvLine -> {
      TrafficPointVersionCsvModel atlasCsvLine = findCorrespondingAtlasServicePointVersion(didokCsvLine,
          groupedAtlasNumbers.get(didokCsvLine.getSloid()));
      new TrafficPointMappingEquality(didokCsvLine, atlasCsvLine).performCheck();
    });
  }

  private TrafficPointVersionCsvModel findCorrespondingAtlasServicePointVersion(TrafficPointElementCsvModel didokCsvLine,
      List<TrafficPointVersionCsvModel> atlasCsvLines) {
    List<TrafficPointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
        atlasCsvLine -> new DateRange(AtlasCsvReader.dateFromString(atlasCsvLine.getValidFrom()),
                AtlasCsvReader.dateFromString(atlasCsvLine.getValidTo())).contains(
            didokCsvLine.getValidFrom())).toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.get(0);
    }
    throw new IllegalStateException("Not exactly one match");
  }
}
