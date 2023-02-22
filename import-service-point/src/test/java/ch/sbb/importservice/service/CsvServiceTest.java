package ch.sbb.importservice.service;

import static ch.sbb.importservice.service.CsvService.DINSTELLE_FILE_PREFIX;
import static ch.sbb.importservice.service.CsvService.LADESTELLEN_FILE_PREFIX;
import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
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
    when(fileHelperService.downloadImportFileFromS3(DINSTELLE_FILE_PREFIX)).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(date, date);
    when(jobHelperService.getDateForImportFileToDownload(IMPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(date);
    //when
    List<ServicePointCsvModelContainer> result = csvService.getActualServicePointCsvModelsFromS3();
    //then
    assertThat(result).hasSize(4);
  }

  @Test
  public void shouldGetActualLoadingPointCsvModelsFromS3() {
    //given
    LocalDate date = MIN_LOCAL_DATE;
    File csvFile = new File(this.getClass().getClassLoader().getResource("LADENSTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(LADESTELLEN_FILE_PREFIX)).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(date, date);
    //when
    List<LoadingPointCsvModel> result = csvService.getActualLoadingPointCsvModelsFromS3();
    //then
    assertThat(result).hasSize(0);
  }

  @Test
  public void shouldGetActualLoadingPointCsvModelsModels() {
    //given
    LocalDate date = MIN_LOCAL_DATE;
    File csvFile = new File(this.getClass().getClassLoader().getResource("LADENSTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3(LADESTELLEN_FILE_PREFIX)).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(date, date);
    //when
    List<LoadingPointCsvModel> result = csvService.getActualLoadingPointCsvModels(csvFile);
    //then
    assertThat(result).hasSize(0);
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
