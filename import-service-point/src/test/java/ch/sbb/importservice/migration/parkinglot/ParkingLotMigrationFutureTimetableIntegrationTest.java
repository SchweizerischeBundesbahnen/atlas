package ch.sbb.importservice.migration.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.ParkingLotCsvService;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
public class ParkingLotMigrationFutureTimetableIntegrationTest {

    private static final String DIDOK_CSV_FILE = "PRM_PARKING_LOTS_20240312011813.csv";
    private static final String ATLAS_CSV_FILE = "future-timetable-parking_lot-2024-03-12.csv";
    private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2024, 3, 12);

    private static final List<ParkingLotCsvModel> didokParkingLotCsvLines = new ArrayList<>();
    private static final List<ParkingLotVersionCsvModel> atlasParkingLotCsvLines = new ArrayList<>();

    private final ParkingLotCsvService referencePointCsvService;

    @Autowired
    public ParkingLotMigrationFutureTimetableIntegrationTest(ParkingLotCsvService referencePointCsvService) {
        this.referencePointCsvService = referencePointCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
            List<ParkingLotCsvModelContainer> referencePointCsvModelContainers = referencePointCsvService.mapToParkingLotCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ParkingLotCsvModel.class));
            didokParkingLotCsvLines.addAll(referencePointCsvModelContainers.stream()
                    .map(ParkingLotCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        assertThat(didokParkingLotCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
            atlasParkingLotCsvLines.addAll(CsvReader.parseCsv(csvStream, ParkingLotVersionCsvModel.class));
        }
        assertThat(atlasParkingLotCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
        atlasParkingLotCsvLines.forEach(atlasCsvLine -> {
            DateRange dateRange = DateRange.builder()
                    .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                    .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                    .build();
            if (!dateRange.contains(FUTURE_TIMETABLE_DATE)) {
                System.out.println("Nicht im Datumsbereich: " + atlasCsvLine);
            }
            assertThat(dateRange.contains(FUTURE_TIMETABLE_DATE)).isTrue();
        });
    }

}
