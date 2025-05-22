package ch.sbb.importservice.controller;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLSX_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportRequest;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.client.LineBulkImportClient;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.client.TrafficPointBulkImportClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.listener.BulkImportJobCompletionListener;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.mail.BulkImporterMailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class BulkImportControllerIntegrationTest extends BaseControllerApiTest {

  private final BulkImportController bulkImportController;
  private final BulkImportRepository bulkImportRepository;

  @MockitoBean
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @MockitoBean
  private TrafficPointBulkImportClient trafficPointBulkImportClient;

  @MockitoBean
  private LineBulkImportClient lineBulkImportClient;

  @MockitoBean
  private AmazonService amazonService;

  @MockitoBean
  private BulkImportJobCompletionListener bulkImportJobCompletionListener;

  @MockitoBean
  private BulkImporterMailService bulkImporterMailService;

  private String todaysDirectory;

  @Autowired
  BulkImportControllerIntegrationTest(BulkImportController bulkImportController, BulkImportRepository bulkImportRepository) {
    this.bulkImportController = bulkImportController;
    this.bulkImportRepository = bulkImportRepository;
  }

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/UPDATE";
    when(servicePointBulkImportClient.bulkImportUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(2)
            .build()));

    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.UPDATE)
        .inNameOf("Test Name")
        .emails(List.of("test@example.com", "techsupport@atlas-sbb.ch"))
        .build();

    MockMultipartFile mockBulkImportRequest = new MockMultipartFile(
        "bulkImportRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        new ObjectMapper().writeValueAsBytes(bulkImportRequest));

    mvc.perform(multipart("/v1/import/bulk")
            .file(new MockMultipartFile("file", "service-point-update.csv", CSV_CONTENT_TYPE, Files.readAllBytes(file.toPath())))
            .file(mockBulkImportRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isAccepted());

    verify(amazonService, times(1)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportUpdate(any());
    verify(bulkImportJobCompletionListener, times(1)).afterJob(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/service-point-update.csv");
  }

  @Test
  void shouldAcceptTrafficPointUpdateBulkImportWithFile() throws Exception {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/TRAFFIC_POINT/UPDATE";
    when(trafficPointBulkImportClient.bulkImportUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(2)
            .build()));

    File file = ImportFiles.getFileByPath("import-files/valid/traffic-point-update.csv");

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.TRAFFIC_POINT)
        .importType(ImportType.UPDATE)
        .inNameOf("Test Name")
        .emails(List.of("test@example.com", "techsupport@atlas-sbb.ch"))
        .build();

    MockMultipartFile mockBulkImportRequest = new MockMultipartFile(
        "bulkImportRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        new ObjectMapper().writeValueAsBytes(bulkImportRequest));

    mvc.perform(multipart("/v1/import/bulk")
            .file(new MockMultipartFile("file", "traffic-point-update.csv", CSV_CONTENT_TYPE,
                Files.readAllBytes(file.toPath())))
            .file(mockBulkImportRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isAccepted());

    verify(amazonService, times(1)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(trafficPointBulkImportClient, atLeastOnce()).bulkImportUpdate(any());
    verify(bulkImportJobCompletionListener, times(1)).afterJob(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/traffic-point-update.csv");
  }

  @ParameterizedTest
  @MethodSource("ch.sbb.importservice.utils.BulkImportTemplateArgumentsData#implementedTemplates")
  void shouldDownloadTemplateSuccessfully(BulkImportConfig importConfig) throws Exception {
    // When
    String urlTemplatePath =
        "/v1/import/bulk/template/" + importConfig.getApplication() + "/" + importConfig.getObjectType() + "/"
            + importConfig.getImportType();
    mvc.perform(MockMvcRequestBuilders.get(urlTemplatePath))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + importConfig.getTemplateFileName() + "\""))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andReturn();
  }

  @ParameterizedTest
  @MethodSource("ch.sbb.importservice.utils.BulkImportTemplateArgumentsData#notImplementedTemplates")
  void shouldDownloadTemplateForNotImplementedUseCasesUnsuccessfully(BulkImportConfig importConfig) throws Exception {
    //given
    String urlTemplatePath =
        "/v1/import/bulk/template/" + importConfig.getApplication() + "/" + importConfig.getObjectType() + "/"
            + importConfig.getImportType();
    //when & then
    mvc.perform(MockMvcRequestBuilders.get(urlTemplatePath))
        .andExpect(status().isNotImplemented())
        .andReturn();
  }

  @Test
  void shouldImportServicePointCreate() throws IOException {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/CREATE";
    File file = ImportFiles.getFileByPath("import-files/valid/create-service-point-2.xlsx");

    MockMultipartFile multipartFile = new MockMultipartFile("file", "create-service-point-2.xlsx", XLSX_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.CREATE)
        .emails(List.of("test-cc@atlas.ch"))
        .build();

    when(servicePointBulkImportClient.bulkImportCreate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(2)
            .build()));
    bulkImportController.startBulkImport(bulkImportRequest, multipartFile);

    List<BulkImport> bulkImports = bulkImportRepository.findAll();
    assertThat(bulkImports).hasSize(1);

    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/create-service-point-2.xlsx.csv");

    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportCreate(any());
  }

  @Test
  void shouldImportServicePointTerminate() throws IOException {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/TERMINATE";
    File file = ImportFiles.getFileByPath("import-files/valid/terminate_service_point.csv");

    MockMultipartFile multipartFile = new MockMultipartFile("file", "terminate_service_point.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.TERMINATE)
        .emails(List.of("test-cc@atlas.ch"))
        .build();

    when(servicePointBulkImportClient.bulkImportTerminate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(2)
            .build()));
    bulkImportController.startBulkImport(bulkImportRequest, multipartFile);

    List<BulkImport> bulkImports = bulkImportRepository.findAll();
    assertThat(bulkImports).hasSize(1);

    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/terminate_service_point.csv");

    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportTerminate(any());
  }

  @Test
  void shouldImportLineUpdate() throws IOException {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/LIDI/LINE/UPDATE";
    File file = ImportFiles.getFileByPath("import-files/valid/line-update.csv");

    MockMultipartFile multipartFile = new MockMultipartFile("file", "line-update.csv", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.LIDI)
        .objectType(BusinessObjectType.LINE)
        .importType(ImportType.UPDATE)
        .emails(List.of("test-cc@atlas.ch"))
        .build();

    when(lineBulkImportClient.lineUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(2)
            .build()));
    bulkImportController.startBulkImport(bulkImportRequest, multipartFile);

    List<BulkImport> bulkImports = bulkImportRepository.findAll();
    assertThat(bulkImports).hasSize(1);

    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/line-update.csv");

    verify(lineBulkImportClient, atLeastOnce()).lineUpdate(any());
  }
}
