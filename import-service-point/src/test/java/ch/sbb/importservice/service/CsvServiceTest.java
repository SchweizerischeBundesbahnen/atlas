package ch.sbb.importservice.service;

import static ch.sbb.importservice.config.SpringBatchConfig.IMPORT_SERVICE_POINT_CSV_JOB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class CsvServiceTest {

  private CsvService csvService;

  @Mock
  private AmazonService amazonService;

  @Mock
  private JobHelperService jobHelperService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    csvService = new CsvService(amazonService, jobHelperService);
  }

  @Test
  void test_downloadShouldNotFoundFile() {
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today))).thenReturn(
        Collections.emptyList());

    String exMessage =
        assertThrows(RuntimeException.class,
            () -> csvService.downloadImportFile("PREFIX_", IMPORT_SERVICE_POINT_CSV_JOB)).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today));
    assertThat(exMessage).isEqualTo("[IMPORT]: Not found file on S3");
  }

  @Test
  void test_downloadShouldFindMoreThanOneFile() {
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today)))
        .thenReturn(List.of("file1", "file2"));

    String exMessage =
        assertThrows(RuntimeException.class,
            () -> csvService.downloadImportFile("PREFIX_", IMPORT_SERVICE_POINT_CSV_JOB)).getLocalizedMessage();
    verify(amazonService).getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today));
    assertThat(exMessage).isEqualTo("[IMPORT]: Found more than 1 file to download on S3");
  }

  @Test
  void test_downloadShouldWork() throws IOException {
    String today = LocalDate.now().toString().replaceAll("-", "");
    when(amazonService.getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today))).thenReturn(List.of("file"));
    when(amazonService.pullFile(eq("file"))).thenReturn(new File("file"));

    File file = csvService.downloadImportFile("PREFIX_", IMPORT_SERVICE_POINT_CSV_JOB);
    verify(amazonService).getS3ObjectKeysFromPrefix(eq("servicepoint_didok"), eq("PREFIX_" + today));
    verify(amazonService).pullFile(eq("file"));
    assertThat(file.getName()).isEqualTo("file");
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnEmptyList() throws IOException {
    csvService.setMatchingDate(LocalDate.of(2023, 1, 1));
    File csv = new File(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, ServicePointCsvModel.class);
    assertThat(csvModelsToUpdate).hasSize(0);
  }

  @Test
  void test_getCsvModelsToUpdate_shouldReturnOneMismatchedCsvModel() throws IOException {
    csvService.setMatchingDate(LocalDate.of(2020, 6, 9));
    File csv = new File(getClass().getClassLoader().getResource("DIENSTSTELLEN_V3_IMPORT.csv").getFile());
    List<ServicePointCsvModel> csvModelsToUpdate = csvService.getCsvModelsToUpdate(csv, ServicePointCsvModel.class);
    assertThat(csvModelsToUpdate).hasSize(1);
    ServicePointCsvModel csvModel = csvModelsToUpdate.get(0);
    assertThat(csvModel.getNummer()).isEqualTo(1542);
  }

}
