package ch.sbb.importservice.controller;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
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
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.client.ServicePointClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.repository.BulkImportRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


class BulkImportControllerTest extends BaseControllerApiTest {

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @MockBean
  private ServicePointClient servicePointBulkImportClient;

  @MockBean
  private AmazonService amazonService;

  private String todaysDirectory;

  @BeforeEach
  void setUp() {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/UPDATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
  }

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    when(servicePointBulkImportClient.bulkImportUpdate(any())).thenReturn(
        List.of(BulkImportItemExecutionResult.builder()
            .lineNumber(1)
            .build()));

    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update.csv");
    mvc.perform(multipart("/v1/import/bulk/SEPODI/SERVICE_POINT/UPDATE")
            .file(new MockMultipartFile("file", "service-point-update.csv", CSV_CONTENT_TYPE, Files.readAllBytes(file.toPath()))))
        .andExpect(status().isAccepted());

    verify(amazonService, times(2)).putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), eq(todaysDirectory));
    verify(servicePointBulkImportClient, atLeastOnce()).bulkImportUpdate(any());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
    BulkImport bulkImport = bulkImportRepository.findAll().getFirst();
    assertThat(bulkImport.getId()).isNotNull();
    assertThat(bulkImport.getImportFileUrl()).isEqualTo(todaysDirectory + "/service-point-update.csv");
  }

  @ParameterizedTest
  @MethodSource("getArgumentsForDifferentTemplates")
  void shouldDownloadTemplateSuccessfully(String classPathResource, String amazonServicePath, String requestPath) throws Exception {
    // Given
    ClassPathResource resource = new ClassPathResource(classPathResource);
    File file = resource.getFile();

    when(amazonService.pullFile(AmazonBucket.BULK_IMPORT, amazonServicePath))
        .thenReturn(file);

    // When
    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(requestPath))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\""))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andReturn();

    // Then
    MockHttpServletResponse response = mvcResult.getResponse();
    byte[] responseContent = response.getContentAsByteArray();

    try (InputStream fileInputStream = new FileInputStream(file)) {
      byte[] fileContent = IOUtils.toByteArray(fileInputStream);
      assertThat(responseContent)
          .as("The downloaded file should match the original file")
          .isEqualTo(fileContent);
    }
  }

  @Test
  void shouldDownloadTemplateWithIncorrectPathVariablesUnsuccessfully() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v1/import/bulk/template/WRONG_VALUE/INCORRECT_VALUE"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void shouldReturnBadRequestWhenAmazonDoesNotFindTemplate() throws Exception {
    // Given
    when(amazonService.pullFile(AmazonBucket.BULK_IMPORT, "templates/service_point/create_service_point.xlsx"))
        .thenReturn(null);

    // When & Then
    mvc.perform(MockMvcRequestBuilders.get("/v1/import/bulk/template/SERVICE_POINT/CREATE"))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  static Stream<Arguments> getArgumentsForDifferentTemplates() {
    return Stream.of(
        Arguments.of("templates/create_service_point.xlsx", "templates/service_point/create_service_point.xlsx",
            "/v1/import/bulk/template/SERVICE_POINT/CREATE"),
        Arguments.of("templates/create_traffic_point.xlsx", "templates/traffic_point/create_traffic_point.xlsx",
            "/v1/import/bulk/template/TRAFFIC_POINT/CREATE"),
        Arguments.of("templates/update_traffic_point.xlsx", "templates/traffic_point/update_traffic_point.xlsx",
            "/v1/import/bulk/template/TRAFFIC_POINT/UPDATE"),
        Arguments.of("templates/update_service_point.xlsx", "templates/service_point/update_service_point.xlsx",
            "/v1/import/bulk/template/SERVICE_POINT/UPDATE"));
  }

}