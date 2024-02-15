package ch.sbb.importservice.migration.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.migration.MigrationUtil;
import ch.sbb.importservice.service.csv.ParkingLotCsvService;
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
public class ParkingLotMigrationIntegrationTest {

    private static final String DIDOK_CSV_FILE = "PRM_PARKING_LOTS_20240215013815.csv";
    private static final String ATLAS_CSV_FILE = "full-parking_lot-2024-02-15.csv";
    private static final List<ParkingLotCsvModel> didokCsvLines = new ArrayList<>();
    private static final List<ParkingLotVersionCsvModel> atlasCsvLines = new ArrayList<>();

    private final ParkingLotCsvService parkingLotCsvService;

    @Autowired
    public ParkingLotMigrationIntegrationTest(ParkingLotCsvService parkingLotCsvService) {
        this.parkingLotCsvService = parkingLotCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
            List<ParkingLotCsvModelContainer> parkingLotCsvModelContainers = parkingLotCsvService.mapToParkingLotCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ParkingLotCsvModel.class));
            didokCsvLines.addAll(parkingLotCsvModelContainers.stream()
                    .map(ParkingLotCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        assertThat(didokCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
            atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, ParkingLotVersionCsvModel.class));
        }
        assertThat(atlasCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveSameParkingLotNumbersInBothCsvs() {
        Set<Integer> didokParkingLotNumbers =
                didokCsvLines.stream().filter(parkingLotCsvModel -> parkingLotCsvModel.getStatus().equals(1))
                        .map(MigrationUtil::removeCheckDigit).collect(Collectors.toSet());
        Set<Integer> atlasParkingLotNumbers = atlasCsvLines.stream().map(ParkingLotVersionCsvModel::getParentNumberServicePoint)
                .collect(Collectors.toSet());
        Set<Integer> difference = atlasParkingLotNumbers.stream().filter(e -> !didokParkingLotNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!difference.isEmpty()) {
            log.error("We have Atlas ParkingLot Numbers, which are not in Didok: {}", difference);
        }
        Set<Integer> differenceDidok = didokParkingLotNumbers.stream().filter(e -> !atlasParkingLotNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!differenceDidok.isEmpty()) {
            log.error("We have Didok ParkingLot Numbers, which are not in Atlas: {}", differenceDidok);
        }
        assertThat(didokParkingLotNumbers).containsExactlyInAnyOrderElementsOf(atlasParkingLotNumbers);
    }

    @Test
    @Order(3)
    void shouldHaveSameValidityOnEachSloid() {
        Map<String, Validity> groupedSloidsDidok = didokCsvLines.stream().collect(
                Collectors.groupingBy(ParkingLotCsvModel::getSloid,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new Validity(
                                        list.stream().map(item -> DateRange.builder()
                                                .from(item.getValidFrom())
                                                .to(item.getValidTo())
                                                .build()
                                        ).collect(Collectors.toList())
                                ).minify())));
        Map<String, Validity> groupedSloidsAtlas = atlasCsvLines.stream().collect(
                Collectors.groupingBy(ParkingLotVersionCsvModel::getSloid,
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
        assertThat(atlasCsvLines).isNotEmpty();
        Map<String, List<ParkingLotVersionCsvModel>> groupedAtlasParkingLots = atlasCsvLines.stream()
                .collect(Collectors.groupingBy(ParkingLotVersionCsvModel::getSloid));

        Map<String, List<ParkingLotCsvModel>> groupedDidokParkingLots = didokCsvLines.stream()
                .collect(Collectors.groupingBy(ParkingLotCsvModel::getSloid));

        groupedDidokParkingLots.values().stream()
                .map(didokCsvItemList -> didokCsvItemList.stream()
                        .max(Comparator.comparing(ParkingLotCsvModel::getValidTo))
                        .orElse(null))
                .filter(Objects::nonNull)
                .forEach(t -> {
                    ParkingLotVersionCsvModel atlasCsvLine = findCorrespondingAtlasParkingLotVersion(t,
                            groupedAtlasParkingLots.get(t.getSloid()));
                    new ParkingLotMappingEquality(t, atlasCsvLine).performCheck();
                });
    }

    private ParkingLotVersionCsvModel findCorrespondingAtlasParkingLotVersion(ParkingLotCsvModel didokCsvLine,
                                                                          List<ParkingLotVersionCsvModel> atlasCsvLines) {
        List<ParkingLotVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
