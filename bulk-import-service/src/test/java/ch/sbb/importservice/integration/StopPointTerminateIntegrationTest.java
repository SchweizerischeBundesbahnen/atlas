package ch.sbb.importservice.integration;

import static ch.sbb.importservice.module.bulkimport.service.BulkImportFileValidationService.CSV_CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportRequest;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.config.mail.MailProducerService;
import ch.sbb.importservice.module.bulkimport.client.StopPointBulkImportClient;
import ch.sbb.importservice.module.bulkimport.controller.BulkImportController;
import ch.sbb.importservice.module.bulkimport.entity.BulkImport;
import ch.sbb.importservice.module.bulkimport.log.BulkImportLogService;
import ch.sbb.importservice.module.bulkimport.log.LogFile;
import ch.sbb.importservice.module.bulkimport.service.ImportFiles;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Slf4j
@IntegrationTest
class StopPointTerminateIntegrationTest {

  @Autowired
  private BulkImportController bulkImportController;

  @MockitoBean
  private StopPointBulkImportClient stopPointBulkImportClient;

  @MockitoBean
  private AmazonService amazonService;

  @MockitoBean
  private UserAdministrationClient userAdministrationClient;

  @MockitoBean
  private MailProducerService mailProducerService;

  @MockitoSpyBean
  private BulkImportLogService bulkImportLogService;

  private final ArgumentCaptor<LogFile> logFileCaptor = ArgumentCaptor.forClass(LogFile.class);

  private String todaysDirectory;

  @BeforeEach
  void setUp() {
    todaysDirectory = "e123456/" + DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN).format(LocalDate.now())
        + "/PRM/STOP_POINT/TERMINATE";
    when(amazonService.putFile(eq(AmazonBucket.BULK_IMPORT), any(File.class), anyString()))
        .thenAnswer(i -> URI.create("https://atlas-bulk-import-dev-dev.s3.eu-central-1.amazonaws.com/" +
            todaysDirectory + "/" + i.getArgument(1, File.class).getName()).toURL());
    when(userAdministrationClient.getCurrentUser()).thenReturn(UserModel.builder().mail("test@atlas.ch").build());
  }

  @Test
  void shouldImportStopPointsAndTerminateViaApi() throws IOException {
    // Given
    when(stopPointBulkImportClient.bulkImportStopPointTerminate(any())).thenAnswer(i -> {
      List<BulkImportUpdateContainer<SloidTerminateCsvModel>> argument = i.getArgument(0, List.class);
      return argument.stream().map(j -> BulkImportItemExecutionResult.builder().lineNumber(j.getLineNumber()).build()).toList();
    });
    File file = ImportFiles.getFileByPath("import-files/valid/stop-point-terminate.csv");

    // When
    MockMultipartFile multipartFile = new MockMultipartFile("file", "stop-point-terminate", CSV_CONTENT_TYPE,
        Files.readAllBytes(file.toPath()));
    BulkImportRequest importRequest = BulkImportRequest.builder()
        .applicationType(ApplicationType.PRM)
        .objectType(BusinessObjectType.STOP_POINT)
        .importType(ImportType.TERMINATE)
        .emails(List.of("test-cc@atlas.ch"))
        .build();
    bulkImportController.startBulkImport(importRequest, multipartFile);

    // Then
    verify(stopPointBulkImportClient, times(1)).bulkImportStopPointTerminate(any());

    verify(bulkImportLogService).writeLogToFile(logFileCaptor.capture(), any(BulkImport.class));
    LogFile writtenLogFile = logFileCaptor.getValue();

    LogFile expected = LogFile.builder().nbOfSuccess(1L).nbOfInfo(0L).nbOfError(0L).build();
    assertThat(writtenLogFile).isEqualTo(expected);
  }

}
