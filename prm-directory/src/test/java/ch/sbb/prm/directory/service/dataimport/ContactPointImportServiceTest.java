package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.ContactPointCsvTestData;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.repository.ContactPointRepository;
import ch.sbb.prm.directory.service.PrmLocationService;
import ch.sbb.prm.directory.service.StopPointService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@IntegrationTest
@Transactional
class ContactPointImportServiceTest {
    public static final String SLOID = "ch:1:sloid:76646";
    @MockBean
    private StopPointService stopPointService;

    @MockBean
    private PrmLocationService prmLocationService;

    private final ContactPointRepository contactPointRepository;
    private final ContactPointImportService contactPointImportService;

    @Autowired
    ContactPointImportServiceTest(ContactPointRepository contactPointRepository,
                                ContactPointImportService contactPointImportService) {
        this.contactPointRepository = contactPointRepository;
        this.contactPointImportService = contactPointImportService;
    }

    @Test
    void shouldImportWhenContactPointDoesNotExists() {
        //when
        List<ItemImportResult> result = contactPointImportService.importContactPoints(
                List.of(ContactPointCsvTestData.getContainer()));

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
        assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
        assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
        assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
        verify(prmLocationService, times(1)).allocateSloid(any(ContactPointVersion.class), eq(SloidType.CONTACT_POINT));
    }

    @Test
    void shouldImportWhenContactPointPointExists() {
        //given
        ContactPointCsvModelContainer contactPointCsvModelContainer = ContactPointCsvTestData.getContainer();
        ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
        contactPointVersion.setSloid(contactPointCsvModelContainer.getSloid());
        contactPointVersion.setParentServicePointSloid(SLOID);
        contactPointVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
        contactPointRepository.saveAndFlush(contactPointVersion);
        doNothing().when(stopPointService).checkStopPointExists(any());
        //when
        List<ItemImportResult> result = contactPointImportService.importContactPoints(List.of(contactPointCsvModelContainer));

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
        assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
        assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
        assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
        verify(prmLocationService, never()).allocateSloid(any(), any());
    }
}
