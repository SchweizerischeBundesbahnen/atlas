package ch.sbb.importservice.migration.toilet;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.ToiletCsvService;
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

@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ToiletMigrationActualDateIntegrationTest {

    private static final String DIDOK_TOILET_CSV_FILE = "PRM_TOILETS_20240214013754.csv";
    private static final String ATLAS_TOILET_CSV_FILE = "actual-date-toilet-2024-02-14.csv";
    private static final LocalDate ACTUAL_DATE = LocalDate.of(2024, 2, 14);

    private static final List<ToiletCsvModel> didokToiletCsvLines = new ArrayList<>();
    private static final List<ToiletVersionCsvModel> atlasToiletCsvLines = new ArrayList<>();

    private final ToiletCsvService toiletCsvService;

    @Autowired
    public ToiletMigrationActualDateIntegrationTest(ToiletCsvService toiletCsvService) {
        this.toiletCsvService = toiletCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try(InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_TOILET_CSV_FILE)) {
            List<ToiletCsvModelContainer> toiletCsvModelContainers = toiletCsvService.mapToToiletCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ToiletCsvModel.class));
                    didokToiletCsvLines.addAll(toiletCsvModelContainers.stream()
                            .map(ToiletCsvModelContainer::getCsvModels)
                            .flatMap(Collection::stream)
                            .toList());
        }
        assertThat(didokToiletCsvLines).isNotEmpty();

        try(InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_TOILET_CSV_FILE)) {
            atlasToiletCsvLines.addAll(CsvReader.parseCsv(csvStream, ToiletVersionCsvModel.class));
        }
        assertThat(atlasToiletCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveOnlyVersionsOnActualDate() {
        atlasToiletCsvLines.forEach(atlasCsvLine ->
                assertThat(
                DateRange.builder()
                        .from(CsvReader.dateFromString(atlasCsvLine.getValidFrom()))
                        .to(CsvReader.dateFromString(atlasCsvLine.getValidTo()))
                        .build()
                        .contains(ACTUAL_DATE)
        ).isTrue());
    }

}
