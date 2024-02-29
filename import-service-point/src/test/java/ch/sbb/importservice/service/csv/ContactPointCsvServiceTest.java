package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.testdata.prm.ContactPointCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ContactPointCsvServiceTest {
    private ContactPointCsvService contactPointCsvService;

    @Mock
    private FileHelperService fileHelperService;

    @Mock
    private JobHelperService jobHelperService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        contactPointCsvService = new ContactPointCsvService(fileHelperService, jobHelperService);
    }

    @Test
    void shouldHaveCorrectFileNameInfoDesk() {
        contactPointCsvService.setFilename("PRM_INFO_DESKS");
        CsvFileNameModel csvFileName = contactPointCsvService.csvFileNameModel();
        assertThat(csvFileName.getFileName()).startsWith("PRM_INFO_DESKS");
    }

    @Test
    void shouldHaveCorrectFileNameTicketCounter() {
        contactPointCsvService.setFilename("PRM_TICKET_COUNTER");
        CsvFileNameModel csvFileName = contactPointCsvService.csvFileNameModel();
        assertThat(csvFileName.getFileName()).startsWith("PRM_TICKET_COUNTER");
    }

    @Test
    void shouldMergeSequentialEqualsContactPoints() {
        // given
        ContactPointCsvModel contactPointCsvModel1 = ContactPointCsvTestData.getCsvModel();
        ContactPointCsvModel contactPointCsvModel2 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel2.setValidFrom(LocalDate.of(2026, 1, 1));
        contactPointCsvModel2.setValidTo(LocalDate.of(2026, 12, 31));
        List<ContactPointCsvModel> csvModels = List.of(contactPointCsvModel1, contactPointCsvModel2);

        // when
        List<ContactPointCsvModelContainer> result = contactPointCsvService.mapToContactPointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(contactPointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(contactPointCsvModel2.getValidTo());
    }

    @Test
    void shouldMergeEqualsContactPoints() {
        // given
        ContactPointCsvModel contactPointCsvModel1 = ContactPointCsvTestData.getCsvModel();
        ContactPointCsvModel contactPointCsvModel2 = ContactPointCsvTestData.getCsvModel();
        List<ContactPointCsvModel> csvModels = List.of(contactPointCsvModel1, contactPointCsvModel2);

        // when
        List<ContactPointCsvModelContainer> result = contactPointCsvService.mapToContactPointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(contactPointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(contactPointCsvModel2.getValidTo());
    }

    @Test
    void shouldReturnOnlyActiveContactPoint() {
        // given
        ContactPointCsvModel contactPointCsvModel1 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel1.setStatus(1);
        ContactPointCsvModel contactPointCsvModel2 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel2.setValidFrom(contactPointCsvModel1.getValidTo().plusDays(1));
        contactPointCsvModel2.setValidTo(contactPointCsvModel1.getValidTo().plusYears(1));
        contactPointCsvModel2.setStatus(0);
        List<ContactPointCsvModel> csvModels = List.of(contactPointCsvModel1,contactPointCsvModel2);

        // when
        List<ContactPointCsvModelContainer> result = contactPointCsvService.mapToContactPointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
    }

    @Test
    void shouldSetReplaceHighDateDataForContactPoints() {
        // given
        ContactPointCsvModel contactPointCsvModel1 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel1.setStatus(1);
        ContactPointCsvModel contactPointCsvModel2 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel2.setValidFrom(contactPointCsvModel1.getValidTo().plusDays(1));
        contactPointCsvModel2.setValidTo(ImportUtils.DIDOK_HIGEST_DATE);
        contactPointCsvModel2.setStatus(1);
        List<ContactPointCsvModel> csvModels = List.of(contactPointCsvModel1,contactPointCsvModel2);
        LocalDate now = LocalDate.now();

        // when
        List<ContactPointCsvModelContainer> result = contactPointCsvService.mapToContactPointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(contactPointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(ImportUtils.ATLAS_HIGHEST_DATE);
        assertThat(result.get(0).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);
    }


    @Test
    void shouldSetReplaceNewLineForContactPoints() {
        // given
        ContactPointCsvModel contactPointCsvModel1 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel1.setDescription("Desc $newline$ of $newline$ this model.");
        contactPointCsvModel1.setStatus(1);
        ContactPointCsvModel contactPointCsvModel2 = ContactPointCsvTestData.getCsvModel();
        contactPointCsvModel2.setValidFrom(contactPointCsvModel1.getValidTo().plusDays(1));
        contactPointCsvModel2.setDescription("Desc $newline$ of $newline$ this model.");
        contactPointCsvModel2.setValidTo(LocalDate.of(2026, 12, 31));
        contactPointCsvModel2.setStatus(1);
        List<ContactPointCsvModel> csvModels = List.of(contactPointCsvModel1,contactPointCsvModel2);
        LocalDate now = LocalDate.now();

        // when
        List<ContactPointCsvModelContainer> result = contactPointCsvService.mapToContactPointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(contactPointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(result.get(0).getCsvModels().get(0).getDescription()).isEqualTo("Desc \r\n of \r\n this model.");
        assertThat(result.get(0).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);
    }

}
