package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModel;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.testdata.prm.ReferencePointCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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

    @Test
    void shouldSetReplaceDataForReferencePoints() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel1();
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel1();
        referencePointCsvModel2.setValidFrom(LocalDate.of(2023, 9, 15));
        referencePointCsvModel2.setValidTo(LocalDate.of(2099, 12, 31));
        referencePointCsvModel2.setCreatedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel2.setModifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        List<ReferencePointCsvModel> csvModels = List.of(referencePointCsvModel1, referencePointCsvModel2);
        LocalDate now = LocalDate.now();

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(referencePointCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(0).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);
    }

    @Test
    void shouldSetReplaceDataForReferencePointsWithoutMerging() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel1();
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel2();
        ReferencePointCsvModel referencePointCsvModel3 = ReferencePointCsvTestData.getCsvModel3();
        List<ReferencePointCsvModel> csvModels = List.of(referencePointCsvModel1, referencePointCsvModel2, referencePointCsvModel3);
        LocalDate now = LocalDate.now();

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(2023, 9, 14));
        assertThat(result.get(0).getCsvModels().get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2023, 9, 18, 12, 48));
        assertThat(result.get(1).getCsvModels()).hasSize(1);
        assertThat(result.get(1).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(1).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);
        assertThat(result.get(2).getCsvModels()).hasSize(1);
        assertThat(result.get(2).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(2).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);
    }

    @Test
    void shouldSetReplaceDataForReferencePointsWithoutMergingAndWithMultipleVersionsProSPN() {
        // given
        ReferencePointCsvModel referencePointCsvModel1 = ReferencePointCsvTestData.getCsvModel1();
        ReferencePointCsvModel referencePointCsvModel2 = ReferencePointCsvTestData.getCsvModel1();
        referencePointCsvModel2.setValidFrom(LocalDate.of(2023, 9, 15));
        referencePointCsvModel2.setValidTo(LocalDate.of(2099, 12, 31));
        referencePointCsvModel2.setCreatedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel2.setModifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48));

        ReferencePointCsvModel referencePointCsvModel11 = ReferencePointCsvTestData.getCsvModel2();
        ReferencePointCsvModel referencePointCsvModel12 = ReferencePointCsvTestData.getCsvModel1();
        referencePointCsvModel12.setValidFrom(LocalDate.of(2023, 1, 1));
        referencePointCsvModel12.setValidTo(LocalDate.of(2023, 7, 31));
        referencePointCsvModel12.setCreatedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel12.setModifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel12.setSloid("ch:1:sloid:5468:270449");
        referencePointCsvModel12.setDsSloid("ch:1:sloid:5468");
        referencePointCsvModel12.setDidokCode(85054686);

        ReferencePointCsvModel referencePointCsvModel111 = ReferencePointCsvTestData.getCsvModel3();
        ReferencePointCsvModel referencePointCsvModel112 = ReferencePointCsvTestData.getCsvModel1();
        referencePointCsvModel112.setValidFrom(LocalDate.of(2023, 1, 1));
        referencePointCsvModel112.setValidTo(LocalDate.of(2023, 7, 31));
        referencePointCsvModel112.setCreatedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel112.setModifiedAt(LocalDateTime.of(2023, 9, 18, 12, 48));
        referencePointCsvModel112.setSloid("ch:1:sloid:5469:270449");
        referencePointCsvModel112.setDsSloid("ch:1:sloid:5469");
        referencePointCsvModel112.setDidokCode(85054696);

        List<ReferencePointCsvModel> csvModels = List.of(
            referencePointCsvModel1, referencePointCsvModel2,
            referencePointCsvModel11, referencePointCsvModel12,
            referencePointCsvModel111, referencePointCsvModel112);
        LocalDate now = LocalDate.now();

        // when
        List<ReferencePointCsvModelContainer> result = referencePointCsvService.mapToReferencePointCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getDidokCode()).isEqualTo(85054676);
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(0).getCsvModels().get(0).getModifiedAt().toLocalDate()).isEqualTo(now);

        assertThat(result.get(1).getCsvModels()).hasSize(2);
        assertThat(result.get(1).getCsvModels().get(0).getDidokCode()).isEqualTo(85054696);
        assertThat(result.get(1).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(2023, 7, 31));
        assertThat(result.get(1).getCsvModels().get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2023, 9, 18, 12, 48));
        assertThat(result.get(1).getCsvModels().get(1).getDidokCode()).isEqualTo(85054696);
        assertThat(result.get(1).getCsvModels().get(1).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(1).getCsvModels().get(1).getModifiedAt().toLocalDate()).isEqualTo(now);

        assertThat(result.get(2).getCsvModels()).hasSize(2);
        assertThat(result.get(2).getCsvModels().get(0).getDidokCode()).isEqualTo(85054686);
        assertThat(result.get(2).getCsvModels().get(0).getValidTo()).isEqualTo(LocalDate.of(2023, 7, 31));
        assertThat(result.get(2).getCsvModels().get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2023, 9, 18, 12, 48));
        assertThat(result.get(2).getCsvModels().get(1).getDidokCode()).isEqualTo(85054686);
        assertThat(result.get(2).getCsvModels().get(1).getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
        assertThat(result.get(2).getCsvModels().get(1).getModifiedAt().toLocalDate()).isEqualTo(now);
    }

}
