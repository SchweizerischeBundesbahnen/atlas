package ch.sbb.importservice.migration.referencepoint;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.ReferencePointCsvService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReferencePointMigrationFutureTimetableIntegrationTest {

    private static final String DIDOK_STOP_PLACE_CSV_FILE = "PRM_REFERENCE_POINTS_TEST_20240201013707.csv";
    private static final String ATLAS_STOP_POINT_CSV_FILE = "future-timetable-reference-point-2024-02-04.csv";
    private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2024, 12, 15);

    private static final List<ReferencePointCsvModel> didokReferencePointCsvLines = new ArrayList<>();
    private static final List<ReferencePointVersionCsvModel> atlasReferencePointCsvLines = new ArrayList<>();

    private final ReferencePointCsvService referencePointCsvService;

    @Autowired
    public ReferencePointMigrationFutureTimetableIntegrationTest(ReferencePointCsvService referencePointCsvService) {
        this.referencePointCsvService = referencePointCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_STOP_PLACE_CSV_FILE)) {
            List<ReferencePointCsvModelContainer> referencePointCsvModelContainers = referencePointCsvService.mapToReferencePointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ReferencePointCsvModel.class));
            didokReferencePointCsvLines.addAll(referencePointCsvModelContainers.stream()
                    .map(ReferencePointCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        assertThat(didokReferencePointCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_STOP_POINT_CSV_FILE)) {
            atlasReferencePointCsvLines.addAll(CsvReader.parseCsv(csvStream, ReferencePointVersionCsvModel.class));
        }
        assertThat(atlasReferencePointCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
        atlasReferencePointCsvLines.forEach(atlasCsvLine -> {
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
