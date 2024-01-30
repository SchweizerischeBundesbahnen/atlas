package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

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
      protected CsvFileNameModel csvFileNameModel() {
        return CsvFileNameModel.builder().fileName("TEST_FILE_PREFIX")
            .s3BucketDir(ServicePointCsvService.SERVICE_POINT_FILE_PREFIX)
            .addDateToPostfix(true)
            .build();
      }

      @Override
      protected String getModifiedDateHeader() {
        return EDITED_AT_COLUMN_NAME_SERVICE_POINT;
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

  @Test
  void shouldGetActualCsvModelsFromS3() {
    //given
    LocalDate date = LocalDate.of(2022, 2, 21);
    File csvFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv")).getFile());
    when(fileHelperService.downloadImportFileFromS3(any())).thenReturn(csvFile);
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
    File csvFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv")).getFile());
    //when
    List<ServicePointCsvModel> result = csvService.getActualCsvModels(csvFile);
    //then
    assertThat(result).hasSize(0);
    verify(fileHelperService, times(0)).downloadImportFileFromS3(csvService.csvFileNameModel());
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnEmptyList() {
    //given
    LocalDate localDate = LocalDate.of(2023, 1, 1);
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(localDate, localDate);
    File csv = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv")).getFile());
    //when
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate);
    //then
    assertThat(csvModelsToUpdate).hasSize(0);
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnOneMismatchedCsvModel() {
    //given
    LocalDate localDate = LocalDate.of(2020, 6, 9);
    File csv = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv")).getFile());
    doCallRealMethod().when(jobHelperService).isDateMatchedBetweenTodayAndMatchingDate(localDate, localDate);
    //when
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, localDate);
    //then
    assertThat(csvModelsToUpdate).hasSize(1);
    ServicePointCsvModel csvModel = csvModelsToUpdate.get(0);
    assertThat(csvModel.getNummer()).isEqualTo(1542);
  }

}
