package ch.sbb.importservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.exception.LogFileNotFoundException;
import ch.sbb.importservice.model.BulkImportResult;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.service.bulk.BulkImportFileValidationService;
import ch.sbb.importservice.service.bulk.BulkImportService;
import ch.sbb.importservice.service.bulk.log.BulkImportLogService;
import ch.sbb.importservice.service.bulk.log.LogFile;
import ch.sbb.importservice.service.bulk.template.BulkImportTemplateGenerator;
import ch.sbb.importservice.service.mail.BulkImporterMailService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BulkImportControllerTest {

  private BulkImportController controller;

  @Mock
  private BulkImportService service;

  @Mock
  private BulkImportLogService bulkImportLogService;

  @Mock
  private BulkImportFileValidationService bulkImportFileValidationService;

  @Mock
  private BulkImportTemplateGenerator bulkImportTemplateGenerator;

  @Mock
  private BulkImporterMailService bulkImporterMailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    controller = new BulkImportController(service, bulkImportLogService, bulkImportFileValidationService,
        bulkImportTemplateGenerator, bulkImporterMailService);
  }

  @Test
  void getBulkImportResults() {
    when(service.getBulkImport(5L)).thenReturn(
        BulkImport.builder()
            .objectType(BusinessObjectType.SERVICE_POINT)
            .creationDate(LocalDateTime.of(2024, 1, 1, 15, 15))
            .creator("test")
            .inNameOf("test chef")
            .importType(ImportType.UPDATE)
            .logFileUrl("/test.log")
            .build()
    );
    when(bulkImportLogService.getLogFileFromS3("/test.log")).thenReturn(LogFile.builder()
        .logEntries(List.of(
            BulkImportLogEntry.builder().build(),
            BulkImportLogEntry.builder().build()
        ))
        .nbOfSuccess(5L)
        .nbOfError(10L)
        .nbOfInfo(15L)
        .build());

    BulkImportResult bulkImportResult = controller.getBulkImportResults(5L);

    assertThat(bulkImportResult.getBusinessObjectType()).isEqualTo(BusinessObjectType.SERVICE_POINT);
    assertThat(bulkImportResult.getCreationDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 15, 15));
    assertThat(bulkImportResult.getCreator()).isEqualTo("test");
    assertThat(bulkImportResult.getInNameOf()).isEqualTo("test chef");
    assertThat(bulkImportResult.getImportType()).isEqualTo(ImportType.UPDATE);
    assertThat(bulkImportResult.getLogEntries()).hasSize(2);
    assertThat(bulkImportResult.getNbOfSuccess()).isEqualTo(5);
    assertThat(bulkImportResult.getNbOfInfo()).isEqualTo(15);
    assertThat(bulkImportResult.getNbOfError()).isEqualTo(10);
  }

  @Test
  void shouldThrowExceptionWhenLogFileIsNull() {
    when(service.getBulkImport(5L)).thenReturn(
        BulkImport.builder()
            .objectType(BusinessObjectType.SERVICE_POINT)
            .creationDate(LocalDateTime.of(2024, 1, 1, 15, 15))
            .creator("test")
            .inNameOf("test chef")
            .importType(ImportType.UPDATE)
            .logFileUrl(null)
            .build());

    assertThatExceptionOfType(LogFileNotFoundException.class).isThrownBy(() -> controller.getBulkImportResults(5L));
  }
}
