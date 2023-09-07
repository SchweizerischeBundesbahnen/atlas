package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

class LoadingPointCsvServiceTest {

  private LoadingPointCsvService loadingPointCsvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = openMocks(this);
    loadingPointCsvService = new LoadingPointCsvService(fileHelperService, jobHelperService);
  }

  @AfterEach
  void teardown() throws Exception {
    mocks.close();
  }

  @Test
  void shouldMapToLoadingPointCsvModelContainersWithPreMerge() {
    // given
    List<LoadingPointCsvModel> csvModels = List.of(
        LoadingPointCsvModel.builder()
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .height(500.88)
            .servicePointNumber(8507000)
            .number(1)
            .build(),
        LoadingPointCsvModel.builder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .height(500.88)
            .servicePointNumber(8507000)
            .number(2)
            .build(),
        LoadingPointCsvModel.builder()
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .height(500.88)
            .servicePointNumber(8507000)
            .number(2)
            .build()
    );

    // when
    List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers =
        loadingPointCsvService.mapToLoadingPointCsvModelContainers(
        csvModels);

    // then
    assertThat(loadingPointCsvModelContainers).hasSize(2);
    assertThat(loadingPointCsvModelContainers.get(0).getCsvModelList()).hasSize(1);
    assertThat(loadingPointCsvModelContainers.get(0).getCsvModelList().get(0).getNumber()).isEqualTo(1);
    assertThat(loadingPointCsvModelContainers.get(0).getCsvModelList().get(0).getServicePointNumber()).isEqualTo(8507000);

    assertThat(loadingPointCsvModelContainers.get(1).getCsvModelList()).hasSize(1);
    assertThat(loadingPointCsvModelContainers.get(1).getCsvModelList().get(0).getNumber()).isEqualTo(2);
    assertThat(loadingPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2021, 1, 1));
    assertThat(loadingPointCsvModelContainers.get(1).getCsvModelList().get(0).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31));
  }

}
