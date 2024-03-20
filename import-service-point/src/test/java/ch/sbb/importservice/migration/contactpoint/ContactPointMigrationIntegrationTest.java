package ch.sbb.importservice.migration.contactpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.util.CsvReader;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Validity;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.migration.MigrationUtil;
import ch.sbb.importservice.service.csv.ContactPointCsvService;
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

//@Disabled
@IntegrationTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactPointMigrationIntegrationTest {

    private static final String DIDOK_CSV_FILE = "PRM_TICKET_COUNTERS_20240320013805.csv";
    private static final String DIDOK_CSV_FILE_INFO_DESK = "PRM_INFO_DESKS_20240320013756.csv";
    private static final String ATLAS_CSV_FILE = "full-contact_point-2024-03-20.csv";
    private static final List<ContactPointCsvModel> didokCsvLines = new ArrayList<>();
    private static final List<ContactPointVersionCsvModel> atlasCsvLines = new ArrayList<>();

    private final ContactPointCsvService contactPointCsvService;

    @Autowired
    public ContactPointMigrationIntegrationTest(ContactPointCsvService contactPointCsvService) {
        this.contactPointCsvService = contactPointCsvService;
    }

    @Test
    @Order(1)
    void shouldParseCsvCorrectly() throws IOException {
        List<ContactPointCsvModel> infoDesks = new ArrayList<>();
        List<ContactPointCsvModel> ticketCounters = new ArrayList<>();
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE)) {
//        try (InputStream csvStream = ContactPointUtil.getInputStream(this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE),
//            this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK))) {
            List<ContactPointCsvModelContainer> contactPointCsvModelContainers =
                contactPointCsvService.mapToContactPointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ContactPointCsvModel.class));
            infoDesks.addAll(contactPointCsvModelContainers.stream()
                .map(ContactPointCsvModelContainer::getCsvModels)
                .flatMap(Collection::stream)
                .toList());
            didokCsvLines.addAll(contactPointCsvModelContainers.stream()
                    .map(ContactPointCsvModelContainer::getCsvModels)
                    .flatMap(Collection::stream)
                    .toList());
        }
        try (InputStream csvStream = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK)) {
            //        try (InputStream csvStream = ContactPointUtil.getInputStream(this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE),
            //            this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK))) {
            List<ContactPointCsvModelContainer> contactPointCsvModelContainers =
                contactPointCsvService.mapToContactPointCsvModelContainers(
                    CsvReader.parseCsv(csvStream, ContactPointCsvModel.class));
            ticketCounters.addAll(contactPointCsvModelContainers.stream()
                .map(ContactPointCsvModelContainer::getCsvModels)
                .flatMap(Collection::stream)
                .toList());
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
    void shouldHaveSameNumbersInBothCsvs() {
        Set<Integer> didokContactPointNumbers =
                didokCsvLines.stream().filter(csvModel -> csvModel.getStatus().equals(1))
                        .map(MigrationUtil::removeCheckDigit).collect(Collectors.toSet());
        Set<Integer> atlasNumbers = atlasCsvLines.stream().map(ContactPointVersionCsvModel::getParentNumberServicePoint)
                .collect(Collectors.toSet());
        Set<Integer> difference = atlasNumbers.stream().filter(e -> !didokContactPointNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!difference.isEmpty()) {
            log.error("HERE ARE ATLAS CONTACT POINTS WHICH ARE NOT IN DIDOK");
            log.error("We have Atlas ContactPoint Numbers, which are not in Didok: {}", difference);
        }
        Set<Integer> differenceDidok = didokContactPointNumbers.stream().filter(e -> !atlasNumbers.contains(e))
                .collect(Collectors.toSet());
        if (!differenceDidok.isEmpty()) {
            log.error("HERE ARE DIDOK CONTACT POINTS WHICH ARE NOT IN ATLAS");
            log.error("We have Didok ContactPoint Numbers, which are not in Atlas: {}", differenceDidok);
        }
        System.out.println("DIDOK CONTACT POINT NUMBERS");
        didokContactPointNumbers.forEach(System.out::println);
        System.out.println("ATLAS NUMBERS");
        atlasNumbers.forEach(System.out::println);
        assertThat(didokContactPointNumbers).containsExactlyInAnyOrderElementsOf(atlasNumbers);
    }

    @Test
    @Order(3)
    void shouldHaveSameValidityOnEachSloid() {
        Map<String, Validity> groupedSloidsDidok = didokCsvLines.stream().collect(
                Collectors.groupingBy(ContactPointCsvModel::getSloid,
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
                Collectors.groupingBy(ContactPointVersionCsvModel::getSloid,
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
        Map<String, List<ContactPointVersionCsvModel>> groupedAtlasContactPoints = atlasCsvLines.stream()
                .collect(Collectors.groupingBy(ContactPointVersionCsvModel::getSloid));

        Map<String, List<ContactPointCsvModel>> groupedDidokContactPoints = didokCsvLines.stream()
                .collect(Collectors.groupingBy(ContactPointCsvModel::getSloid));

        groupedDidokContactPoints.values().stream()
                .map(didokCsvItemList -> didokCsvItemList.stream()
                        .max(Comparator.comparing(ContactPointCsvModel::getValidTo))
                        .orElse(null))
                .filter(Objects::nonNull)
                .forEach(t -> {
                    ContactPointVersionCsvModel atlasCsvLine = findCorrespondingAtlasContactPointVersion(t,
                            groupedAtlasContactPoints.get(t.getSloid()));
                    new ContactPointMappingEquality(t, atlasCsvLine).performCheck();
                });
    }

    private ContactPointVersionCsvModel findCorrespondingAtlasContactPointVersion(ContactPointCsvModel didokCsvLine,
                                                                          List<ContactPointVersionCsvModel> atlasCsvLines) {
        List<ContactPointVersionCsvModel> matchedVersions = atlasCsvLines.stream().filter(
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
