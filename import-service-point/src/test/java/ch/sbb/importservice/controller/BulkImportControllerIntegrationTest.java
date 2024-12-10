package ch.sbb.importservice.controller;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.XLSX_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.client.TrafficPointBulkImportClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BulkImportRequest;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.repository.BulkImportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class BulkImportControllerIntegrationTest extends BaseControllerApiTest {

  @Autowired
  private BulkImportController bulkImportController;

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @MockBean
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @MockBean
  private TrafficPointBulkImportClient trafficPointBulkImportClient;

  @MockBean
  private AmazonService amazonService;

  private String todaysDirectory;

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/UPDATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
    when(servicePointBulkImportClient.bulkImportUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(1)
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

    verify(amazonService, times(2)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportUpdate(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/service-point-update.csv");
  }

  @Test
  void shouldAcceptTrafficPointUpdateBulkImportWithFile() throws Exception {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/TRAFFIC_POINT/UPDATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
    when(trafficPointBulkImportClient.bulkImportUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(1)
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

    verify(amazonService, times(2)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(trafficPointBulkImportClient, atLeastOnce()).bulkImportUpdate(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/traffic-point-update.csv");
  }

  @ParameterizedTest
  @MethodSource("getArgumentsImplementedTemplates")
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
  @MethodSource("getArgumentsForNotImplementedTemplates")
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

  static Stream<Arguments> getArgumentsForNotImplementedTemplates() {
    return Stream.of(
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.TERMINATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.CREATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.CREATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.TERMINATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.CREATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.UPDATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.LOADING_POINT, ImportType.TERMINATE))
    );
  }

  static Stream<Arguments> getArgumentsImplementedTemplates() {
    return Stream.of(
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.UPDATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.SEPODI, BusinessObjectType.TRAFFIC_POINT, ImportType.UPDATE)),
        Arguments.of(new BulkImportConfig(ApplicationType.PRM, BusinessObjectType.PLATFORM_REDUCED, ImportType.UPDATE))
    );
  }

  @Test
  void shouldImportServicePointCreate() throws IOException {
    File file = ImportFiles.getFileByPath("import-files/valid/create-service-point-2.xlsx");

    MockMultipartFile multipartFile = new MockMultipartFile("file", "create-service-point-2.xlsx", XLSX_CONTENT_TYPE, Files.readAllBytes(file.toPath()));

    BulkImportRequest bulkImportRequest = BulkImportRequest.builder()
            .applicationType(ApplicationType.SEPODI)
            .objectType(BusinessObjectType.SERVICE_POINT)
            .importType(ImportType.CREATE)
            .emails(List.of("test-cc@atlas.ch"))
            .build();

    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
            .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
                    todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());

    when(servicePointBulkImportClient.bulkImportCreate(any())).thenReturn(
            List.of(BulkImportItemExecutionResult.builder()
                    .lineNumber(1)
                    .build()));

    bulkImportController.startServicePointImportBatch(bulkImportRequest, multipartFile);

    List<BulkImport> bulkImports = bulkImportRepository.findAll();
    assertThat(bulkImports).hasSize(1);

    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/create-service-point-2.xlsx.csv");

    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportCreate(any());

  }
}
