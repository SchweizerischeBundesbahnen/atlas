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
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.ImportFiles;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.repository.BulkImportRepository;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.log.PersistedLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;

@Slf4j
@IntegrationTest
class BulkImportLogFileIntegrationTest {

  @Autowired
  private BulkImportController bulkImportController;

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @MockBean
  private AmazonService amazonService;

  @SpyBean
  private PersistedLogService persistedLogService;

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
  }

  /**
   * service-point-update-mix.csv contains the following lines
   * 1 - updates successfully
   * 2 - has all the data validation errors
   * 3 - has all the type mapping problems possible
   * 4 - Returns Entity not found simulated by mock
   * 5 - Mocks no update during Versioning
   */
  @Test
  void shouldImportMixFileAndCreateLogFileCorrectly() throws IOException {
    // Given
    setupSepodiResponsesForMix();
    File file = ImportFiles.getFileByPath("import-files/valid/service-point-update-mix.csv");

    // When
    MockMultipartFile multipartFile = new MockMultipartFile("file", "service-point"
        + "-update-mix.csv", CSV_CONTENT_TYPE, Files.readAllBytes(file.toPath()));
    bulkImportController.startServicePointImportBatch(ApplicationType.SEPODI, BusinessObjectType.SERVICE_POINT, ImportType.UPDATE,
        multipartFile);

    // Then
    assertThat(bulkImportRepository.count()).isEqualTo(1);
    verify(persistedLogService).writeLogToFile(logFileCaptor.capture(), any(BulkImport.class));

    LogFile writtenLogFile = logFileCaptor.getValue();

    File expectedLogFile = ImportFiles.getFileByPath("import-files/valid/service-point-update-mix.log");
    LogFile expected = objectMapper.readValue(expectedLogFile, LogFile.class);

    assertThat(writtenLogFile).isEqualTo(expected);
  }

  private void setupSepodiResponsesForMix() {
    when(servicePointBulkImportClient.bulkImportUpdate(any())).thenAnswer(i -> {
      List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> argument = i.getArgument(0, List.class);
      List<BulkImportItemExecutionResult> answer = new ArrayList<>();
      argument.forEach(container -> {
        switch (container.getLineNumber()) {
          case 1 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(1).build());
          case 4 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(4).errorResponse(ErrorResponse.builder()
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
          case 5 -> answer.add(BulkImportItemExecutionResult.builder().lineNumber(5).errorResponse(ErrorResponse.builder()
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