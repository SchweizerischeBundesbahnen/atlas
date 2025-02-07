package ch.sbb.importservice.controller;

import static ch.sbb.importservice.service.bulk.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BulkImportRequest;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.bulk.log.BulkImportLogService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.mail.MailProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Slf4j
@IntegrationTest
class BulkImportLogFileIntegrationTest {

  @Autowired
  private BulkImportController bulkImportController;

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @MockitoBean
  private AmazonService amazonService;

  @MockitoBean
  private UserAdministrationClient userAdministrationClient;

  @MockitoBean
  private MailProducerService mailProducerService;

  @MockitoSpyBean
  private BulkImportLogService bulkImportLogService;

  @Captor
  private ArgumentCaptor<LogFile> logFileCaptor;

  private String todaysDirectory;

  @BeforeEach
  void setUp() {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/SEPODI/SERVICE_POINT/UPDATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
    when(userAdministrationClient.getCurrentUser()).thenReturn(UserModel.builder().mail("test@atlas.ch").build());
  }

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  /**
   * service-point-update-mix.csv contains the following lines
   * 1 - updates successfully
   * 2 - has all the data validation errors
   * 3 - has all the type mapping problems possible
   * 4 - Returns Entity not found simulated by mock
   * 5 - Mocks no update during Versioning
   * 6 & 7 - Duplicated sloid
   * 8 & 9 - Duplicated number
   */
  @Test
  void shouldImportMixFileAndCreateLogFileCorrectly() throws IOException {
    // Given
    setupSepodiResponsesForMix();
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update-mix.csv");

    // When
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point"
        + "-update-mix.csv", CSV_CONTENT_TYPE, Files.readAllBytes(file.toPath()));
    BulkImportRequest importRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.SEPODI)
        .objectType(BusinessObjectType.SERVICE_POINT)
        .importType(ImportType.UPDATE)
        .emails(List.of("test-cc@atlas.ch"))
        .build();
    bulkImportController.startBulkImport(importRequest, multipartFile);

    // Then
    List<BulkImport> bulkImports = bulkImportRepository.findAll();
    assertThat(bulkImports).hasSize(1);
    BulkImport bulkImport = bulkImports.getFirst();
    verify(bulkImportLogService).writeLogToFile(logFileCaptor.capture(), any(BulkImport.class));

    LogFile writtenLogFile = logFileCaptor.getValue();

    File expectedLogFile = ImportFiles.getFileByPath("import-files/valid/service-point-update-mix.log");
    LogFile expected = objectMapper.readValue(expectedLogFile, LogFile.class);

    assertThat(writtenLogFile).isEqualTo(expected);
    verify(mailProducerService).produceMailNotification(eq(MailNotification.builder()
        .to(List.of("test@atlas.ch"))
        .cc(List.of("test-cc@atlas.ch"))
        .subject("Import Result " + bulkImport.getId())
        .mailType(MailType.BULK_IMPORT_RESULT_NOTIFICATION)
        .templateProperties(List.of(
            Map.of(
                "url", "http://localhost:4200/bulk-import/" + bulkImport.getId(),
                "applicationTypeDe", "Dienststellen",
                "applicationTypeFr", "points de services",
                "applicationTypeIt", "posto di servizio",
                "objectTypeDe", "Dienststelle",
                "objectTypeFr", "service",
                "objectTypeIt", "posto di servizio",
                "importTypeDe", "aktualisiert",
                "importTypeFr", "mises à jour",
                "importTypeIt", "aggiornati"
            )
        ))
        .build()));
  }

  private void setupSepodiResponsesForMix() {
    when(servicePointBulkImportClient.bulkImportUpdate(any())).thenAnswer(i -> {
      List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> argument = i.getArgument(0);
      List<BulkImportItemExecutionResult> answer = new ArrayList<>();
      argument.forEach(container -> {
        switch (container.getLineNumber()) {
          case 2 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(2).build());
          case 5 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(5).errorResponse(ErrorResponse.builder()
                  .error("Not found")
                  .details(new TreeSet<>(Set.of(Detail.builder()
                      .message("Object with SLOID ch:1:sloid:notfound not found")
                      .displayInfo(DisplayInfo.builder()
                          .code("ERROR.ENTITY_NOT_FOUND")
                          .with("field", "sloid")
                          .with("value", "ch:1:sloid:notfound")
                          .build())
                      .build())))
                  .build())
              .build());
          case 6 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(6).errorResponse(ErrorResponse.builder()
                  .status(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS)
                  .error("No entities were modified after versioning execution.")
                  .details(new TreeSet<>(Set.of(Detail.builder()
                      .message("No entities were modified after versioning execution.")
                      .displayInfo(DisplayInfo.builder().code("ERROR.WARNING.VERSIONING_NO_CHANGES").build())
                      .build())))
                  .build())
              .build());
        }
      });
      return answer;
    });
  }
}
