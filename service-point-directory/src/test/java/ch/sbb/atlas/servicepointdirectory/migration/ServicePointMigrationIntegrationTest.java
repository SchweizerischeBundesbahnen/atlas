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
import org.junit.jupiter.api.Test;

@IntegrationTest
@Slf4j
public class ServicePointMigrationIntegrationTest {

  private static final String DIDOK_CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20230629021731.csv";
  private static final String ATLAS_CSV_FILE = "full-world-service-point-2023-06-29.csv";
  private static final String SEPARATOR = "/";

  @Test
  void shouldMigrateCorrectly() throws IOException {
    List<ServicePointCsvModel> didokCsvLines = new ArrayList<>();
    List<ServicePointVersionCsvModel> atlasCsvLines = new ArrayList<>();

    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + DIDOK_CSV_FILE)) {
      didokCsvLines.addAll(ServicePointImportService.parseServicePoints(csvStream));
    }
    assertThat(didokCsvLines).hasSize(363850);

    try (InputStream csvStream = this.getClass().getResourceAsStream(SEPARATOR + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(AtlasCsvReader.parseAtlasServicePoints(csvStream));
    }
    assertThat(atlasCsvLines).hasSize(362640);

    shouldHaveSameDidokCodesInBothCsvs(didokCsvLines, atlasCsvLines);
    shouldHaveSameValidityOnEachDidokCode(didokCsvLines, atlasCsvLines);
  }

  void shouldHaveSameDidokCodesInBothCsvs(List<ServicePointCsvModel> didokCsvLines,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    assertThat(didokCsvLines).hasSize(363850);
    assertThat(atlasCsvLines).hasSize(362640);

    Set<Integer> didokCodes = didokCsvLines.stream().map(ServicePointCsvModel::getDidokCode).collect(Collectors.toSet());
    Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ServicePointVersionCsvModel::getNumber).collect(Collectors.toSet());

    assertThat(didokCodes).containsExactlyInAnyOrderElementsOf(atlasNumbers);
  }

  void shouldHaveSameValidityOnEachDidokCode(List<ServicePointCsvModel> didokCsvLines,
      List<ServicePointVersionCsvModel> atlasCsvLines) {
    assertThat(didokCsvLines).hasSize(363850);
    assertThat(atlasCsvLines).hasSize(362640);

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

  private LocalDate fromString(String string) {
    return LocalDate.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH));
  }

}
