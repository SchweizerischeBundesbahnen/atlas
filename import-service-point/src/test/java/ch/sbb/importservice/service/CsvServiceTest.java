package ch.sbb.importservice.service;

import static ch.sbb.importservice.service.CsvService.DIENSTELLEN_FILE_PREFIX;
import static ch.sbb.importservice.service.CsvService.LADESTELLEN_FILE_PREFIX;
import static ch.sbb.importservice.service.CsvService.VERKEHRSPUNKTELEMENTE_FILE_PREFIX;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class CsvServiceTest {

  private CsvService csvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    csvService = new CsvService(fileHelperService, jobHelperService);
  }

  @Test
  public void shouldGetActualServicePointCsvModelsFromS3() {
    //given
    LocalDate date = LocalDate.of(2022, 2, 21);
    File csvFile = new File(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(DIENSTELLEN_FILE_PREFIX)).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(any(), any());
    when(jobHelperService.getDateForImportFileToDownload(IMPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(date);
    //when
    List<ServicePointCsvModelContainer> result = csvService.getActualServicePointCsvModelsFromS3();
    //then
    assertThat(result).hasSize(4);
  }

  @Test
  public void shouldGetActualLoadingPointCsvModelsFromS3() {
    //given
    LocalDate date = LocalDate.of(2018, 6, 27);
    File csvFile = new File(this.getClass().getClassLoader().getResource("LADESTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(LADESTELLEN_FILE_PREFIX)).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(any(), any());
    when(jobHelperService.getDateForImportFileToDownload(IMPORT_LOADING_POINT_CSV_JOB_NAME)).thenReturn(date);
    //when
    List<LoadingPointCsvModel> result = csvService.getActualLoadingPointCsvModelsFromS3();
    //then
    assertThat(result).hasSize(12);
  }

  @Test
  public void shouldGetActualLoadingPointCsvModels() {
    //given
    File csvFile = new File(this.getClass().getClassLoader().getResource("LADESTELLEN_V3_IMPORT.csv").getFile());
    //when
    List<LoadingPointCsvModel> result = csvService.getActualLoadingPointCsvModels(csvFile);
    //then
    assertThat(result).hasSize(0);
    verify(fileHelperService, times(0)).downloadImportFileFromS3(LADESTELLEN_FILE_PREFIX);
  }

  @Test
  public void shouldGetActualTrafficPointCsvModelsFromS3() {
    //given
    File csvFile = new File(this.getClass().getClassLoader().getResource("VERKEHRSPUNKTELEMENTE_IMPORT.csv")
        .getFile());
    when(fileHelperService.downloadImportFileFromS3(VERKEHRSPUNKTELEMENTE_FILE_PREFIX)).thenReturn(csvFile);
    //when
    List<TrafficPointElementCsvModel> result = csvService.getActualTrafficPointCsvModelsFromS3();
    //then
    assertThat(result).hasSize(0);
  }

  @Test
  public void shouldGetActualTrafficPointCsvModels() {
    //given
    File csvFile = new File(this.getClass().getClassLoader().getResource("VERKEHRSPUNKTELEMENTE_IMPORT.csv")
        .getFile());
    //when
    List<LoadingPointCsvModel> result = csvService.getActualLoadingPointCsvModels(csvFile);
    //then
    assertThat(result).hasSize(0);
    verify(fileHelperService, times(0))
        .downloadImportFileFromS3(VERKEHRSPUNKTELEMENTE_FILE_PREFIX);
  }

  @Test
  void shouldMapToTrafficPointCsvModelContainersWithPreMerge() {
    // given
    List<TrafficPointElementCsvModel> csvModels = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:123")
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .height(500.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .height(500.88)
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:567")
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .height(500.88)
            .build()
    );

    // when
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = csvService.mapToTrafficPointCsvModelContainers(
        csvModels);

    // then
    assertThat(trafficPointCsvModelContainers).hasSize(2);
    assertThat(trafficPointCsvModelContainers.get(0).getSloid()).isEqualTo("ch:1:sloid:123");
    assertThat(trafficPointCsvModelContainers.get(0).getTrafficPointCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(0).getTrafficPointCsvModelList().get(0).getSloid()).isEqualTo("ch:1:sloid:123");

    assertThat(trafficPointCsvModelContainers.get(1).getSloid()).isEqualTo("ch:1:sloid:567");
    assertThat(trafficPointCsvModelContainers.get(1).getTrafficPointCsvModelList()).hasSize(1);
    assertThat(trafficPointCsvModelContainers.get(1).getTrafficPointCsvModelList().get(0).getSloid()).isEqualTo("ch:1:sloid:567");
    assertThat(trafficPointCsvModelContainers.get(1).getTrafficPointCsvModelList().get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2021, 1, 1));
    assertThat(trafficPointCsvModelContainers.get(1).getTrafficPointCsvModelList().get(0).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31));
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnEmptyList() {
    //given
    LocalDate localDate = LocalDate.of(2023, 1, 1);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(localDate, localDate);
    File csv = new File(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    //when
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate, ServicePointCsvModel.class);
    //then
    assertThat(csvModelsToUpdate).hasSize(0);
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnOneMismatchedCsvModel() {
    //given
    LocalDate localDate = LocalDate.of(2020, 6, 9);
    File csv = new File(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(localDate, localDate);
    //when
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate, ServicePointCsvModel.class);
    //then
    assertThat(csvModelsToUpdate).hasSize(1);
    ServicePointCsvModel csvModel = csvModelsToUpdate.get(0);
    assertThat(csvModel.getNummer()).isEqualTo(1542);
  }

}
