package ch.sbb.importservice.migration.contactpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.service.csv.ContactPointCsvService;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
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
public class ContactPointMigrationFutureTimetableIntegrationTest {

    private static final String DIDOK_CSV_FILE = "PRM_TICKET_COUNTERS_20240320013805.csv";
    private static final String DIDOK_CSV_FILE_INFO_DESK = "PRM_INFO_DESKS_20240320013756.csv";
    private static final String ATLAS_CSV_FILE = "future-timetable-contact_point-2024-03-20.csv";
    private static final LocalDate FUTURE_TIMETABLE_DATE = LocalDate.of(2024, 12, 15);

    private static final List<ContactPointCsvModel> didokCsvLines = new ArrayList<>();
    private static final List<ContactPointVersionCsvModel> atlasCsvLines = new ArrayList<>();

    private final ContactPointCsvService contactPointCsvService;

    @Autowired
    public ContactPointMigrationFutureTimetableIntegrationTest(ContactPointCsvService contactPointCsvService) {
        this.contactPointCsvService = contactPointCsvService;
    }
    public static InputStream removeFirstNLines(InputStream inputStream, int linesToSkip) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            // Skip the first linesToSkip lines
            for (int i = 0; i < linesToSkip; i++) {
                reader.readLine();
            }

            // Return the modified InputStream
            return new ByteArrayInputStream(reader.readLine().getBytes()); // just an example, adjust as needed
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    InputStream getInputStream() throws IOException{
        InputStream inputStream1 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE);

//        String outputFile = removeFirstLines(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK, CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK_MOD);
//        FileInputStream inputStream2 = new FileInputStream(outputFile);
//        InputStream inputStream2 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK_MOD);



        InputStream inputStream2 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK);
        InputStream modifiedInputStream = removeFirstNLines(inputStream2, 7);

        // Concatenate input streams
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStream1, modifiedInputStream);

        // Convert the sequenceInputStream to a regular InputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = sequenceInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        InputStream concatenatedInputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Use the concatenated input stream as needed

        // Close the streams
        sequenceInputStream.close();
        concatenatedInputStream.close();
        return concatenatedInputStream;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
            List<ContactPointCsvModelContainer> contactPointCsvModelContainers =
                contactPointCsvService.mapToContactPointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ContactPointCsvModel.class));
            didokCsvLines.addAll(contactPointCsvModelContainers.stream()
                .map(ContactPointCsvModelContainer::getCsvModels)
                .flatMap(Collection::stream)
                .toList());
        }
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK)) {
            List<ContactPointCsvModelContainer> contactPointCsvModelContainers =
                contactPointCsvService.mapToContactPointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ContactPointCsvModel.class));
            didokCsvLines.addAll(contactPointCsvModelContainers.stream()
                .map(ContactPointCsvModelContainer::getCsvModels)
                .flatMap(Collection::stream)
                .toList());
        }
        assertThat(didokCsvLines).isNotEmpty();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + ATLAS_CSV_FILE)) {
            atlasCsvLines.addAll(CsvReader.parseCsv(csvStream, ContactPointVersionCsvModel.class));
        }
        assertThat(atlasCsvLines).isNotEmpty();
    }

    @Test
    @Order(2)
    void shouldHaveOnlyVersionsValidOnFutureTimetableDate() {
        atlasCsvLines.forEach(atlasCsvLine -> {
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
