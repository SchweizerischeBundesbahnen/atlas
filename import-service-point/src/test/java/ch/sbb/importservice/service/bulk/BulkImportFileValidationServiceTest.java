package ch.sbb.importservice.service.bulk;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLSX_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLS_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.exception.ContentTypeFileValidationException;
import ch.sbb.importservice.exception.FileHeaderValidationException;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

@IntegrationTest
class BulkImportFileValidationServiceTest {

  @Autowired
  private BulkImportFileValidationService bulkImportFileValidationService;

  @Test
  void shouldReturnOnValidCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File result = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, ServicePointUpdate.CONFIG);
    assertThat(result.length()).isEqualTo(file.length());
  }

  @Test
  void shouldReturnOnValidXlsFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.xls");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update.xls", XLS_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File csvFile = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, ServicePointUpdate.CONFIG);
    ImportFiles.assertThatFileContainsExpectedServicePointUpdate(csvFile);
  }

  @Test
  void shouldReturnOnValidXlsxFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.xlsx");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update.xlsx", XLSX_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File csvFile = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, ServicePointUpdate.CONFIG);
    ImportFiles.assertThatFileContainsExpectedServicePointUpdate(csvFile);
  }

  @Test
  void shouldReportInvalidFileHeaderOnCsv() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-update-invalid-header.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update-invalid-header.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(FileHeaderValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            ServicePointUpdate.CONFIG));
  }

  @Test
  void shouldReportInvalidFileHeaderOnXlsx() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-update-invalid-header.xlsx");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update-invalid-header.xlsx", XLSX_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(FileHeaderValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            ServicePointUpdate.CONFIG));
  }

  @Test
  void shouldReportInvalidContentTypeOnTxt() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/service-point-update-invalid-content-type.txt");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update-invalid-content-type.txt", "plain/txt",
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(ContentTypeFileValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            ServicePointUpdate.CONFIG));
  }
}