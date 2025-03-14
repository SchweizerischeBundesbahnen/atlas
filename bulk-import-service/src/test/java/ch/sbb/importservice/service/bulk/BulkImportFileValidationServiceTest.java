package ch.sbb.importservice.service.bulk;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLSX_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLS_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.exception.ContentTypeFileValidationException;
import ch.sbb.importservice.exception.FileHeaderValidationException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.template.BulkImportTemplateGenerator;
import ch.sbb.importservice.service.bulk.template.ServicePointTemplateGenerator;
import ch.sbb.importservice.service.prm.platform.update.PlatformUpdate;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdate;
import ch.sbb.importservice.service.sepodi.traffic.point.update.TrafficPointUpdate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

@IntegrationTest
class BulkImportFileValidationServiceTest {

  @Autowired
  private BulkImportFileValidationService bulkImportFileValidationService;

  private ServicePointTemplateGenerator servicePointTemplateGenerator;

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

    File result = bulkImportFileValidationService.validateFileAndPrepareFile(multipartFile, PlatformUpdate.CONFIG);
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
            TrafficPointUpdate.CONFIG));
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

  @Test
  void shouldReturnValidFileHeaderServicePointCreate() {
    BulkImportConfig bulkImportConfig = BulkImportConfig.builder()
        .importType(ImportType.CREATE)
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .build();

    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }

  @Test
  void shouldReturnValidFileHeaderServicePointUpdate() {
    BulkImportConfig bulkImportConfig = BulkImportConfig.builder()
        .importType(ImportType.UPDATE)
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .build();

    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }

  @Test
  void shouldReturnValidFileHeaderTrafficPointCreate() {
    BulkImportConfig bulkImportConfig = BulkImportConfig.builder()
        .importType(ImportType.CREATE)
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.TRAFFIC_POINT)
        .build();

    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }

  @Test
  void shouldReturnValidFileHeaderTrafficPointUpdate() {
    BulkImportConfig bulkImportConfig = BulkImportConfig.builder()
        .importType(ImportType.UPDATE)
        .application(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.TRAFFIC_POINT)
        .build();

    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }

  @Test
  void shouldReturnValidFileHeaderPlatformReducedUpdate() {
    BulkImportConfig bulkImportConfig = BulkImportConfig.builder()
        .importType(ImportType.UPDATE)
        .application(ApplicationType.PRM)
        .objectType(BusinessObjectType.PLATFORM_REDUCED)
        .build();

    Mockito.when(fileService.getDir()).thenReturn("./export/");

    File file = bulkImportTemplateGenerator.generateCsvTemplate(bulkImportConfig);

    assertDoesNotThrow(() -> bulkImportFileValidationService.validateFileHeader(file,
        bulkImportConfig));
  }
}