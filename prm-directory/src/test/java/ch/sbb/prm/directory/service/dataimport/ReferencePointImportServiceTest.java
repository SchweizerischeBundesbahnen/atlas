package ch.sbb.prm.directory.service.dataimport;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.testdata.prm.ReferencePointCsvTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.service.StopPointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@IntegrationTest
@Transactional
class ReferencePointImportServiceTest {

    @MockBean
    private StopPointService stopPointService;

    private final ReferencePointRepository referencePointRepository;
    private final ReferencePointImportService referencePointImportService;


    @Autowired
    ReferencePointImportServiceTest(ReferencePointRepository referencePointRepository,
                                    ReferencePointImportService referencePointImportService) {
        this.referencePointRepository = referencePointRepository;
        this.referencePointImportService = referencePointImportService;
    }

    @BeforeEach
    void setUp() {
        when(stopPointService.isReduced(any())).thenReturn(false);
    }

    @AfterEach
    void cleanUp() {
    }

    @Test
    void shouldImportWhenReferencePointDoesNotExists() {
        //when
        List<ItemImportResult> result = referencePointImportService.importReferencePoints(List.of(ReferencePointCsvTestData.getContainer()));

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
        assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
        assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
        assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    }

    @Test
    void shouldImportWhenReferencePointExists() {
        //given
        ReferencePointCsvModelContainer referencePointCsvModelContainer = ReferencePointCsvTestData.getContainer();
        ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
        referencePointVersion.setSloid(referencePointCsvModelContainer.getSloid());
        referencePointVersion.setParentServicePointSloid("ch:1:sloid:76646");
        referencePointVersion.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8576646));
        referencePointRepository.saveAndFlush(referencePointVersion);

        //when
        List<ItemImportResult> result = referencePointImportService.importReferencePoints(List.of(referencePointCsvModelContainer));

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("[SUCCESS]: This version was imported successfully");
        assertThat(result.get(0).getItemNumber()).isEqualTo("8500294");
        assertThat(result.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 25));
        assertThat(result.get(0).getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.get(0).getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    }

}
