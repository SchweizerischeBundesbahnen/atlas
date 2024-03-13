package ch.sbb.importservice.migration.relation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.entity.RelationKeyId;
import ch.sbb.importservice.service.csv.RelationCsvService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

//@Disabled
@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RelationMigrationIntegrationTest {

//  private static final String DIDOK_CSV_FILE = "PRM_CONNECTIONS_20240312011830.csv";
//  private static final String ATLAS_CSV_FILE = "full-relation-2024-03-12.csv";

  private static final String DIDOK_CSV_FILE = "PRM_CONNECTIONS_20240306013727.csv";
  private static final String ATLAS_CSV_FILE = "full-relation-2024-03-06.csv";
//  private static final String DIDOK_CSV_FILE = "PRM_CONNECTIONS_20240312011830.csv";
//  private static final String ATLAS_CSV_FILE = "full-relation-2024-03-12.csv";

  private static final List<RelationCsvModel> didokCsvLines = new ArrayList<>();
  private static final List<RelationVersionCsvModel> atlasCsvLines = new ArrayList<>();

  private final RelationCsvService relationCsvService;

  @Autowired
  public RelationMigrationIntegrationTest(RelationCsvService relationCsvService) {
    this.relationCsvService = relationCsvService;
  }

  @Test
  @Order(1)
  void shouldParseCsvCorrectly() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
      List<RelationCsvModelContainer> relationCsvModelContainers = relationCsvService.mapToRelationCsvModelContainers(
          CsvReader.parseCsv(csvStream, RelationCsvModel.class));
      didokCsvLines.addAll(
          relationCsvModelContainers.stream().map(RelationCsvModelContainer::getCsvModels).flatMap(Collection::stream).toList());
    }
    assertThat(didokCsvLines).isNotEmpty();
    try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
      atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, RelationVersionCsvModel.class));
    }
    assertThat(atlasCsvLines).isNotEmpty();
  }

  @Test
  @Order(3)
  void shouldHaveSameValidityOnEachSloidCombination() {
    Map<RelationKeyId, Validity> groupedSloidsDidok = didokCsvLines.stream().collect(
        Collectors.groupingBy(i -> new RelationKeyId(i.getSloid(), i.getRpSloid()),
            Collectors.collectingAndThen(Collectors.toList(), list -> new Validity(
                list.stream().map(item -> DateRange.builder().from(item.getValidFrom()).to(item.getValidTo()).build())
                    .collect(Collectors.toList())).minify())));
    Map<RelationKeyId, Validity> groupedSloidsAtlas = atlasCsvLines.stream().collect(
        Collectors.groupingBy(i -> new RelationKeyId(i.getElementSloid(), i.getReferencePointSloid()),
            Collectors.collectingAndThen(Collectors.toList(), list -> new Validity(list.stream().map(
                i -> DateRange.builder().from(CsvReader.dateFromString(i.getValidFrom()))
                    .to(CsvReader.dateFromString(i.getValidTo())).build()).collect(Collectors.toList())).minify())));
    List<String> validityErrors = new ArrayList<>();

    groupedSloidsDidok.forEach((relationKeyId, didokValidity) -> {
      Validity atlasValidity = groupedSloidsAtlas.get(relationKeyId);
      if (atlasValidity == null) {
        log.warn("Relation {} was not imported to atlas: ", relationKeyId);
      } else if (!atlasValidity.equals(didokValidity)) {
        validityErrors.add(
            "ValidityError on relationKeyId: " + relationKeyId + " didokValidity=" + didokValidity.getDateRanges()
                + ", atlasValidity=" + atlasValidity.getDateRanges());
      }
    });
    if (!validityErrors.isEmpty()) {
      log.error("{}", validityErrors);
    }
    assertThat(validityErrors).isEmpty();
  }

  @Test
  @Order(4)
  void shouldHaveMappedFieldsToAtlasCorrectly() {
    assertThat(atlasCsvLines).isNotEmpty();
    Map<RelationKeyId, List<RelationVersionCsvModel>> groupedAtlasLines = atlasCsvLines.stream()
        .collect(Collectors.groupingBy(i -> new RelationKeyId(i.getElementSloid(), i.getReferencePointSloid())));

    Map<RelationKeyId, List<RelationCsvModel>> groupedDidokLines = didokCsvLines.stream()
        .collect(Collectors.groupingBy(i -> new RelationKeyId(i.getSloid(), i.getRpSloid())));

    groupedDidokLines.values().stream()
        .map(didokCsvItemList -> didokCsvItemList.stream().max(Comparator.comparing(RelationCsvModel::getValidTo)).orElse(null))
        .filter(Objects::nonNull).forEach(t -> {
          RelationKeyId key = new RelationKeyId(t.getSloid(), t.getRpSloid());
          List<RelationVersionCsvModel> versionsInAtlas = groupedAtlasLines.get(key);

          if (versionsInAtlas == null) {
            log.warn("{} could not be found in atlas, reference point may be inactive", key);
          } else {
            RelationVersionCsvModel atlasCsvLine = findCorrespondingAtlasVersion(t, versionsInAtlas);
            new RelationMappingEquality(t, atlasCsvLine).performCheck();
          }
        });
  }

  private RelationVersionCsvModel findCorrespondingAtlasVersion(RelationCsvModel didokCsvLine,
      List<RelationVersionCsvModel> atlasCsvLines) {
    List<RelationVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
        atlasCsvLine -> DateRange.builder().from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
            .to(CsvReader.dateFromString(atlasCsvLine.getValidTo())).build().contains(didokCsvLine.getValidFrom())).toList();
    if (matchedVersions.size() == 1) {
      return matchedVersions.getFirst();
    }
    throw new IllegalStateException("Not exactly one match");
  }

}
