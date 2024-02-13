package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModel;
import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
import ch.sbb.atlas.testdata.prm.ToiletCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ToiletCsvServiceTest {

  private ToiletCsvService toiletCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    toiletCsvService = new ToiletCsvService(fileHelperService, jobHelperService);
  }

  @Test
  void shouldHaveCorrectFileName() {
    CsvFileNameModel csvFileName = toiletCsvService.csvFileNameModel();
    assertThat(csvFileName.getFileName()).startsWith("PRM_TOILETS");
  }

  @Test
  void shouldMergeSequentialEqualsToilets() {
    // given
    ToiletCsvModel toiletCsvModel = ToiletCsvTestData.getCsvModel();
    ToiletCsvModel toiletCsvModel1 = ToiletCsvTestData.getCsvModel();
    toiletCsvModel1.setValidFrom(LocalDate.of(2001, 1, 1));
    toiletCsvModel1.setValidTo(LocalDate.of(2001, 12, 31));
    List<ToiletCsvModel> csvModels = List.of(toiletCsvModel, toiletCsvModel1);

    // when
    List<ToiletCsvModelContainer> result = toiletCsvService.mapToToiletCsvModelContainers(csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(toiletCsvModel.getValidFrom());
    assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(toiletCsvModel1.getValidTo());
  }

  @Test
  void shouldMergeEqualsToilet() {
    // given
    ToiletCsvModel toiletCsvModel1 = ToiletCsvTestData.getCsvModel();
    ToiletCsvModel toiletCsvModel2 = ToiletCsvTestData.getCsvModel();
    List<ToiletCsvModel> csvModels = List.of(toiletCsvModel1, toiletCsvModel2);

    // when
    List<ToiletCsvModelContainer> result = toiletCsvService.mapToToiletCsvModelContainers(csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(toiletCsvModel1.getValidFrom());
    assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(toiletCsvModel2.getValidTo());
  }

  @Test
  void shouldReturnOnlyActiveToilets() {
    // given
    ToiletCsvModel toiletCsvModel1 = ToiletCsvTestData.getCsvModel();
    toiletCsvModel1.setStatus(1);
    ToiletCsvModel toiletCsvModel2 = ToiletCsvTestData.getCsvModel();
    toiletCsvModel2.setValidFrom(toiletCsvModel1.getValidTo().plusDays(1));
    toiletCsvModel2.setValidTo(toiletCsvModel1.getValidTo().plusYears(1));
    toiletCsvModel2.setStatus(0);
    List<ToiletCsvModel> csvModels = List.of(toiletCsvModel1,toiletCsvModel2);

    // when
    List<ToiletCsvModelContainer> result = toiletCsvService.mapToToiletCsvModelContainers(csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCsvModels()).hasSize(1);
    assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
  }

}
