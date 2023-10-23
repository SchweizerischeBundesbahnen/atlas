package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class StopPointCsvServiceTest {

  private StopPointCsvService stopPointCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    stopPointCsvService = new StopPointCsvService(fileHelperService, jobHelperService);
  }

  @Test
  void shouldMergeSequentialEqualsStopPoints() {
    // given
    StopPointCsvModel stopPointCsvModel1 = StopPointCsvTestData.getStopPointCsvModel();
    StopPointCsvModel stopPointCsvModel2 = StopPointCsvTestData.getStopPointCsvModel();
    stopPointCsvModel2.setValidFrom(LocalDate.of(2001, 1, 1));
    stopPointCsvModel2.setValidTo(LocalDate.of(2001, 12, 31));
    List<StopPointCsvModel> csvModels = List.of(stopPointCsvModel1, stopPointCsvModel2);

    // when
    List<StopPointCsvModelContainer> result = stopPointCsvService.mapToStopPointCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels()).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels().get(0).getValidFrom()).isEqualTo(stopPointCsvModel1.getValidFrom());
    assertThat(result.get(0).getStopPointCsvModels().get(0).getValidTo()).isEqualTo(stopPointCsvModel2.getValidTo());

  }

  @Test
  void shouldMergeEqualsStopPoints() {
    // given
    StopPointCsvModel stopPointCsvModel1 = StopPointCsvTestData.getStopPointCsvModel();
    StopPointCsvModel stopPointCsvModel2 = StopPointCsvTestData.getStopPointCsvModel();
    List<StopPointCsvModel> csvModels = List.of(stopPointCsvModel1, stopPointCsvModel2);

    // when
    List<StopPointCsvModelContainer> result = stopPointCsvService.mapToStopPointCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels()).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels().get(0).getValidFrom()).isEqualTo(stopPointCsvModel1.getValidFrom());
    assertThat(result.get(0).getStopPointCsvModels().get(0).getValidTo()).isEqualTo(stopPointCsvModel2.getValidTo());

  }

  @Test
  void shouldReplaceWrongMeansOfTransportCode() {
    // given
    StopPointCsvModel stopPointCsvModel1 = StopPointCsvTestData.getStopPointCsvModel();
    stopPointCsvModel1.setTransportationMeans("~0~");
    List<StopPointCsvModel> csvModels = List.of(stopPointCsvModel1);

    // when
    List<StopPointCsvModelContainer> result = stopPointCsvService.mapToStopPointCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels()).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels().get(0).getTransportationMeans()).isEqualTo("~U~");

  }

  @Test
  void shouldReturnOnlyActiveStopPlaces() {
    // given
    StopPointCsvModel stopPointCsvModel1 = StopPointCsvTestData.getStopPointCsvModel();
    stopPointCsvModel1.setStatus(1);
    StopPointCsvModel stopPointCsvModel2 = StopPointCsvTestData.getStopPointCsvModel();
    stopPointCsvModel2.setValidFrom(stopPointCsvModel1.getValidTo().plusDays(1));
    stopPointCsvModel2.setValidTo(stopPointCsvModel1.getValidTo().plusYears(1));
    stopPointCsvModel2.setStatus(0);
    List<StopPointCsvModel> csvModels = List.of(stopPointCsvModel1,stopPointCsvModel2);

    // when
    List<StopPointCsvModelContainer> result = stopPointCsvService.mapToStopPointCsvModelContainers(
        csvModels);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels()).hasSize(1);
    assertThat(result.get(0).getStopPointCsvModels().get(0).getStatus()).isEqualTo(1);

  }

}
