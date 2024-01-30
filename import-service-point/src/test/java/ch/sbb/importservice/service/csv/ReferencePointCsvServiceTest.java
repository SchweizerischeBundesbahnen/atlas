package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.testdata.prm.ReferencePointCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

class ReferencePointCsvServiceTest {

    private ReferencePointCsvService referencePointCsvService;

    @Mock
    private FileHelperService fileHelperService;

    @Mock
    private JobHelperService jobHelperService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        referencePointCsvService = new ReferencePointCsvService(fileHelperService, jobHelperService);
    }

    @Test
    void shouldHaveCorrectFileName() {
        CsvFileNameModel csvFileName = referencePointCsvService.csvFileNameModel();
        assertThat(csvFileName.getFileName()).startsWith("PRM_REFERENCE_POINTS");
    }

    @Test
    void shouldMergeSequentialEqualsReferencePoints() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel();
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel();
        referencePointCsvModel2.setValidFrom(LocalDate.of(2026, 1, 1));
        referencePointCsvModel2.setValidTo(LocalDate.of(2026, 12, 31));
        List<ReferencePointCsvModel> csvModels = List.of(referencePointCsvModel1, referencePointCsvModel2);

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(referencePointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(referencePointCsvModel2.getValidTo());
    }

    @Test
    void shouldMergeEqualsReferencePoints() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel();
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel();
        List<ReferencePointCsvModel> csvModels = List.of(referencePointCsvModel1, referencePointCsvModel2);

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(
                csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(referencePointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(referencePointCsvModel2.getValidTo());
    }

    @Test
    void shouldReturnOnlyActiveReferencePoints() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel();
        referencePointCsvModel1.setStatus(1);
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel();
        referencePointCsvModel2.setValidFrom(referencePointCsvModel1.getValidTo().plusDays(1));
        referencePointCsvModel2.setValidTo(referencePointCsvModel1.getValidTo().plusYears(1));
        referencePointCsvModel2.setStatus(0);
        List<ReferencePointCsvModel> csvModels = List.of(referencePointCsvModel1,referencePointCsvModel2);

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(
                csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
    }

}
