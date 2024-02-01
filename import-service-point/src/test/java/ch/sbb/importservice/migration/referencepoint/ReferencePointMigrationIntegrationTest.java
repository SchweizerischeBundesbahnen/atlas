package ch.sbb.importservice.migration.referencepoint;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.migration.MigrationUtil;
import ch.sbb.importservice.service.csv.ReferencePointCsvService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReferencePointMigrationIntegrationTest {

    private static final String DIDOK_REFERENCE_POINT_CSV_FILE = "PRM_REFERENCE_POINTS_TEST_20240201013707.csv";
    private static final String ATLAS_REFERENCE_POINT_CSV_FILE = "full-reference-point-int-2024-02-01.csv";
    private static final List<ReferencePointCsvModel> didokReferencePointCsvLines = new ArrayList<>();
    private static final List<ReferencePointVersionCsvModel> atlasReferencePointCsvLines = new ArrayList<>();

    private final ReferencePointCsvService referencePointCsvService;

    @Autowired
    public ReferencePointMigrationIntegrationTest(ReferencePointCsvService referencePointCsvService) {
        this.referencePointCsvService = referencePointCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_REFERENCE_POINT_CSV_FILE)) {
            List<ReferencePointCsvModelContainer> referencePointCsvModelContainers = referencePointCsvService.mapToReferencePointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ReferencePointCsvModel.class));
            didokReferencePointCsvLines.addAll(referencePointCsvModelContainers.stream()
                    .map(ReferencePointCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        assertThat(didokReferencePointCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_REFERENCE_POINT_CSV_FILE)) {
            atlasReferencePointCsvLines.addAll(CsvReader.parseCsv(csvStream, ReferencePointVersionCsvModel.class));
        }
        assertThat(atlasReferencePointCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveSameReferencePointNumbersInBothCsvs() {
        Set<Integer> didokReferencePointNumbers =
                didokReferencePointCsvLines.stream().filter(referencePointCsvModel -> referencePointCsvModel.getStatus().equals(1))
                        .map(MigrationUtil::removeCheckDigit).collect(Collectors.toSet());
        Set<Integer> atlasReferencePointNumbers = atlasReferencePointCsvLines.stream().map(ReferencePointVersionCsvModel::getParentNumberServicePoint)
                .collect(Collectors.toSet());
        Set<Integer> difference = atlasReferencePointNumbers.stream().filter(e -> !didokReferencePointNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!difference.isEmpty()) {
            log.error("We have Atlas Reference Point Numbers, which are not in Didok: {}", difference);
        }
        Set<Integer> differenceDidok = didokReferencePointNumbers.stream().filter(e -> !atlasReferencePointNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!differenceDidok.isEmpty()) {
            log.error("We have Didok Reference Point Numbers, which are not in Atlas: {}", differenceDidok);
        }
        assertThat(didokReferencePointNumbers).containsExactlyInAnyOrderElementsOf(atlasReferencePointNumbers);
    }

    @Test
    @Order(3)
    void shouldHaveSameValidityOnEachSloid() {
        Map<String, Validity> groupedSloidsDidok = didokReferencePointCsvLines.stream().collect(
                Collectors.groupingBy(ReferencePointCsvModel::getSloid,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new Validity(
                                        list.stream().map(item -> DateRange.builder()
                                                .from(item.getValidFrom())
                                                .to(item.getValidTo())
                                                .build()
                                        ).collect(Collectors.toList())
                                ).minify())));
        Map<String, Validity> groupedSloidsAtlas = atlasReferencePointCsvLines.stream().collect(
                Collectors.groupingBy(ReferencePointVersionCsvModel::getSloid,
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
                log.error("error: ", didokValidity.getDateRanges());
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
        assertThat(atlasReferencePointCsvLines).isNotEmpty();
        Map<String, List<ReferencePointVersionCsvModel>> groupedAtlasReferencePoints = atlasReferencePointCsvLines.stream()
                .collect(Collectors.groupingBy(ReferencePointVersionCsvModel::getSloid));

        Map<String, List<ReferencePointCsvModel>> groupedDidokReferencePoints = didokReferencePointCsvLines.stream()
                .collect(Collectors.groupingBy(ReferencePointCsvModel::getSloid));

        for (List<ReferencePointCsvModel> didokCsvItemList : groupedDidokReferencePoints.values()) {
            Comparator<ReferencePointCsvModel> employeeAgeComparator = Comparator
                    .comparing(ReferencePointCsvModel::getValidTo);

            ReferencePointCsvModel t = didokCsvItemList.stream().max(employeeAgeComparator).get();
            ReferencePointVersionCsvModel atlasCsvLine = findCorrespondingAtlasReferencePointVersion(t,
                    groupedAtlasReferencePoints.get(t.getSloid()));
            new ReferencePointMappingEquality(t, atlasCsvLine).performCheck();
        }
    }

    private ReferencePointVersionCsvModel findCorrespondingAtlasReferencePointVersion(ReferencePointCsvModel didokCsvLine,
                                                                          List<ReferencePointVersionCsvModel> atlasCsvLines) {
        List<ReferencePointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
