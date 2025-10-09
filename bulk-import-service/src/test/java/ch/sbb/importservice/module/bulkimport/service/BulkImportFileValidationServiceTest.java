package ch.sbb.importservice.module.bulkimport.service;

import static ch.sbb.importservice.module.bulkimport.service.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static ch.sbb.importservice.module.bulkimport.service.BulkImportFileValidationService.XLSX_CONTENT_TYPE;
import static ch.sbb.importservice.module.bulkimport.service.BulkImportFileValidationService.XLS_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.module.bulkimport.exception.ContentTypeFileValidationException;
import ch.sbb.importservice.module.bulkimport.exception.FileHeaderValidationException;
import ch.sbb.importservice.module.bulkimport.job.prm.platform.update.complete.PlatformCompleteUpdate;
import ch.sbb.importservice.module.bulkimport.job.prm.platform.update.reduced.PlatformReducedUpdate;
import ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.update.ServicePointUpdate;
import ch.sbb.importservice.module.bulkimport.job.sepodi.trafficpoint.update.TrafficPointUpdate;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import ch.sbb.importservice.module.bulkimport.template.BulkImportTemplateGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

@IntegrationTest
class BulkImportFileValidationServiceTest {

  @Autowired
  private BulkImportFileValidationService bulkImportFileValidationService;

  @Mock
  private FileService fileService;

  private BulkImportTemplateGenerator bulkImportTemplateGenerator;

  @BeforeEach
  void setUp() {
    bulkImportTemplateGenerator = new BulkImportTemplateGenerator(fileService);
  }

  @Test
  void shouldReturnOnValidCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File result = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, ServicePointUpdate.CONFIG);
    assertThat(result.length()).isEqualTo(file.length());
  }

  @Test
  void shouldValidateSuccessfullyValidTrafficPointUpdateCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/traffic-point-update.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "traffic-point-update.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File result = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, TrafficPointUpdate.CONFIG);
    assertThat(result).hasSize(file.length());
  }

  @Test
  void shouldValidateSuccessfullyValidPlatformReducedUpdateCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/platform-reduced-update.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "platform-reduced-update.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    File result = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, PlatformReducedUpdate.CONFIG);
    assertThat(result).hasSize(file.length());
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
  void shouldReportInvalidFileHeaderOnTrafficPointUpdateInvalidHeaderCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/traffic-point-update-invalid-header.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "traffic-point-update-invalid-header.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(FileHeaderValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            TrafficPointUpdate.CONFIG));
  }

  @Test
  void shouldReportInvalidFileHeaderOnPlatformReducedUpdateInvalidHeaderCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/platform-reduced-update-invalid-header.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "platform-reduced-update-invalid-header.csv",
        CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(FileHeaderValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            PlatformReducedUpdate.CONFIG));
  }

  @Test
  void shouldReportInvalidFileHeaderOnPlatformCompleteUpdateInvalidHeaderCsvFile() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/invalid/update_platform_complete_invalid.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "update_platform_complete_invalid.csv",
        CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatExceptionOfType(FileHeaderValidationException.class).isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile,
            PlatformCompleteUpdate.CONFIG));
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
  void shouldIgnoreBomAtFileStart() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update-with-bom.csv");
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point-update-with-bom.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    assertThatNoException().isThrownBy(
        () -> bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, ServicePointUpdate.CONFIG));
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

  @ParameterizedTest
  @MethodSource("ch.sbb.importservice.module.bulkimport.controller.BulkImportTemplateArgumentsData#implementedTemplates")
  void shouldReturnValidFileHeaderServicePointCreate(BulkImportConfig bulkImportConfig) {
    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }
}
