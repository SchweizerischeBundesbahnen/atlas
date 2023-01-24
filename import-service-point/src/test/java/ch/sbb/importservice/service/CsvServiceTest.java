package ch.sbb.importservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
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
