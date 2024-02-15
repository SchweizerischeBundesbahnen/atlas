package ch.sbb.importservice.migration.toilet;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.migration.MigrationUtil;
import ch.sbb.importservice.service.csv.ToiletCsvService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ToiletMigrationIntegrationTest {

    private static final String DIDOK_TOILET_CSV_FILE = "PRM_TOILETS_20240214013754.csv";
    private static final String ATLAS_TOILET_CSV_FILE = "full-toilet-2024-02-14.csv";
    private static final List<ToiletCsvModel> didokToiletCsvLines = new ArrayList<>();
    private static final List<ToiletVersionCsvModel> atlasToiletCsvLines = new ArrayList<>();

    private final ToiletCsvService toiletCsvService;

    @Autowired
    public ToiletMigrationIntegrationTest(ToiletCsvService toiletCsvService) {
        this.toiletCsvService = toiletCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_TOILET_CSV_FILE)) {
            List<ToiletCsvModelContainer> toiletCsvModelContainers =
                toiletCsvService.mapToToiletCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ToiletCsvModel.class));
            didokToiletCsvLines.addAll(toiletCsvModelContainers.stream()
                    .map(ToiletCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        assertThat(didokToiletCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_TOILET_CSV_FILE)) {
            atlasToiletCsvLines.addAll(CsvReader.parseCsv(csvStream, ToiletVersionCsvModel.class));
        }
        assertThat(atlasToiletCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveSameToieltNumbersInBothCsvs() {
        Set<Integer> didokToiletNumbers =
                didokToiletCsvLines.stream().filter(toiletCsvModel -> toiletCsvModel.getStatus().equals(1))
                        .map(toiletCsvModel -> MigrationUtil.removeCheckDigit(toiletCsvModel.getDidokCode())).collect(Collectors.toSet());
        Set<Integer> atlasToiletNumbers = atlasToiletCsvLines.stream().map(ToiletVersionCsvModel::getParentNumberServicePoint)
                .collect(Collectors.toSet());
        Set<Integer> difference = atlasToiletNumbers.stream().filter(e -> !didokToiletNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!difference.isEmpty()) {
            log.error("We have Atlas Toilet Numbers, which are not in Didok: {}", difference);
        }
        Set<Integer> differenceDidok = didokToiletNumbers.stream().filter(e -> !atlasToiletNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!differenceDidok.isEmpty()) {
            log.error("We have Didok Toilet Numbers, which are not in Atlas: {}", differenceDidok);
        }
        assertThat(didokToiletNumbers).containsExactlyInAnyOrderElementsOf(atlasToiletNumbers);
    }

    @Test
    @Order(3)
    void shouldHaveSameValidityOnEachSloid() {
        Map<String, Validity> groupedSloidsDidok = didokToiletCsvLines.stream().collect(
                Collectors.groupingBy(ToiletCsvModel::getSloid,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new Validity(
                                        list.stream().map(item -> DateRange.builder()
                                                .from(item.getValidFrom())
                                                .to(item.getValidTo())
                                                .build()
                                        ).collect(Collectors.toList())
                                ).minify())));
        Map<String, Validity> groupedSloidsAtlas = atlasToiletCsvLines.stream().collect(
                Collectors.groupingBy(ToiletVersionCsvModel::getSloid,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> new Validity(
                                        list.stream().map(
                                                        i -> DateRange.builder()
                                                                .from(CsvReader.dateFromString(i.getValidFrom()))
                                                                .to(CsvReader.dateFromString(i.getValidTo())).build())
                                                .collect(Collectors.toList())).minify())));
        List<String> validityErrors = new ArrayList<>();
        groupedSloidsDidok.forEach((sloid, didokValidity) -> {
            Validity atlasValidity = groupedSloidsAtlas.get(sloid);
            if (atlasValidity == null || !atlasValidity.equals(didokValidity)) {
                log.error("error: " + didokValidity.getDateRanges());
                assert atlasValidity != null;
                validityErrors.add(
                        "ValidityError on sloid: " + sloid + " didokValidity=" + didokValidity.getDateRanges() + ", atlasValidity=" + atlasValidity.getDateRanges());
            }
        });
        if (!validityErrors.isEmpty()) {
            log.error("{}", validityErrors);
        }
        assertThat(validityErrors).isEmpty();
    }

    @Test
    @Order(4)
    void shouldHaveMappedFieldsToAtlasUsingSloidCorrectly() {
        assertThat(atlasToiletCsvLines).isNotEmpty();
        Map<String, List<ToiletVersionCsvModel>> groupedAtlasToilets = atlasToiletCsvLines.stream()
                .collect(Collectors.groupingBy(ToiletVersionCsvModel::getSloid));

        Map<String, List<ToiletCsvModel>> groupedDidokToilets = didokToiletCsvLines.stream()
                .collect(Collectors.groupingBy(ToiletCsvModel::getSloid));

        groupedDidokToilets.values().stream()
                .map(didokCsvItemList -> didokCsvItemList.stream()
                        .max(Comparator.comparing(ToiletCsvModel::getValidTo))
                        .orElse(null))
                .filter(Objects::nonNull)
                .forEach(t -> {
                    ToiletVersionCsvModel atlasCsvLine = findCorrespondingAtlasToiletVersion(t,
                            groupedAtlasToilets.get(t.getSloid()));
                    new ToiletMappingEquality(t, atlasCsvLine).performCheck();
                });
    }

    private ToiletVersionCsvModel findCorrespondingAtlasToiletVersion(ToiletCsvModel didokCsvLine,
                                                                          List<ToiletVersionCsvModel> atlasCsvLines) {
        List<ToiletVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
