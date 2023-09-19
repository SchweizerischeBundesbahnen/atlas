package ch.sbb.importservice.service.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

 class CsvServiceTest {

  private CsvService<ServicePointCsvModel> csvService;

  @Mock
  private FileHelperService fileHelperService;

  @Mock
  private JobHelperService jobHelperService;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = openMocks(this);
    csvService = new CsvService<>(fileHelperService, jobHelperService) {
      @Override
      protected String getFilePrefix() {
        return "TEST_FILE_PREFIX";
      }

      @Override
      protected String getImportCsvJobName() {
        return "TEST_IMPORT_CSV_JOB";
      }

      @Override
      protected Class<ServicePointCsvModel> getType() {
        return ServicePointCsvModel.class;
      }
    };
  }

  @AfterEach
  void teardown() throws Exception {
    mocks.close();
  }

  @Test
   void shouldGetActualCsvModelsFromS3() {
    //given
    LocalDate date = LocalDate.of(2022, 2, 21);
    File csvFile = new File(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    when(fileHelperService.downloadImportFileFromS3("TEST_FILE_PREFIX")).thenReturn(csvFile);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(any(), any());
    when(jobHelperService.getDateForImportFileToDownload("TEST_IMPORT_CSV_JOB")).thenReturn(date);
    //when
    List<ServicePointCsvModel> result = csvService.getActualCsvModelsFromS3();
    //then
    assertThat(result).hasSize(4);
  }

  @Test
   void shouldGetActualCsvModels() {
    //given
    File csvFile = new File(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    //when
    List<ServicePointCsvModel> result = csvService.getActualCsvModels(csvFile);
    //then
    assertThat(result).hasSize(0);
    verify(fileHelperService, times(0)).downloadImportFileFromS3("TEST_FILE_PREFIX");
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnEmptyList() {
    //given
    LocalDate localDate = LocalDate.of(2023, 1, 1);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(localDate, localDate);
    File csv = new File(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    //when
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate);
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
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate);
    //then
    assertThat(csvModelsToUpdate).hasSize(1);
    ServicePointCsvModel csvModel = csvModelsToUpdate.get(0);
    assertThat(csvModel.getNummer()).isEqualTo(1542);
  }

}
