package ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.globalid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.module.bulkimport.client.ServicePointBulkImportClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicePointGlobalIdUpdateWriterTest {

  @Mock
  private ServicePointBulkImportClient servicePointBulkImportClient;

  @Captor
  private ArgumentCaptor<List<BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel>>> containerCaptor;

  @InjectMocks
  private ServicePointGlobalIdUpdateWriter writer;

  private static BulkImportUpdateContainer<?> container(int lineNumber, String globalId) {
    return BulkImportUpdateContainer.<ServicePointGlobalIdUpdateCsvModel>builder()
        .lineNumber(lineNumber)
        .object(ServicePointGlobalIdUpdateCsvModel.builder().number(1105770).globalId(globalId).build())
        .build();
  }

  private static BulkImportItemExecutionResult success(int lineNumber) {
    return BulkImportItemExecutionResult.builder().lineNumber(lineNumber).build();
  }

  private static BulkImportItemExecutionResult withStatus(int lineNumber, int status) {
    return BulkImportItemExecutionResult.builder()
        .lineNumber(lineNumber)
        .errorResponse(ErrorResponse.builder().status(status).message("boom").error("boom").build())
        .build();
  }

  @Test
  void shouldBeRegisteredForTheSepodiGlobalIdUpdateScenario() {
    assertThat(writer.getBulkImportConfig().getApplication()).isEqualTo(ApplicationType.SEPODI);
    assertThat(writer.getBulkImportConfig().getObjectType()).isEqualTo(BusinessObjectType.SERVICE_POINT_GLOBAL_ID);
    assertThat(writer.getBulkImportConfig().getImportType()).isEqualTo(ImportType.UPDATE);
  }

  @Test
  void shouldForwardEveryContainerToTheServicePointDirectory() {
    // Given
    List<BulkImportUpdateContainer<?>> items = List.of(container(2, "de:05770:1282"), container(3, "de:05770:1283"));
    when(servicePointBulkImportClient.bulkImportGlobalIdUpdate(anyList()))
        .thenReturn(List.of(success(2), success(3)));

    // When
    writer.accept(items);

    // Then
    verify(servicePointBulkImportClient).bulkImportGlobalIdUpdate(containerCaptor.capture());
    assertThat(containerCaptor.getValue()).hasSize(2);
    assertThat(containerCaptor.getValue()).extracting(container -> container.getObject().getGlobalId())
        .containsExactly("de:05770:1282", "de:05770:1283");
  }

  @Test
  void shouldMarkSuccessfullyImportedLinesAsSuccess() {
    // Given
    BulkImportUpdateContainer<?> item = container(2, "de:05770:1282");
    when(servicePointBulkImportClient.bulkImportGlobalIdUpdate(anyList())).thenReturn(List.of(success(2)));

    // When
    writer.accept(List.of(item));

    // Then
    assertThat(item.getBulkImportLogEntry().getStatus()).isEqualTo(BulkImportStatus.SUCCESS);
    assertThat(item.getBulkImportLogEntry().getLineNumber()).isEqualTo(2);
  }

  @Test
  void shouldKeepRepointingInfoAsInfoRatherThanTurningItIntoAnError() {
    // Given
    BulkImportUpdateContainer<?> item = container(2, "de:05770:1282");
    when(servicePointBulkImportClient.bulkImportGlobalIdUpdate(anyList()))
        .thenReturn(List.of(withStatus(2, ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS)));

    // When
    writer.accept(List.of(item));

    // Then
    assertThat(item.getBulkImportLogEntry().getStatus()).isEqualTo(BulkImportStatus.INFO);
  }

  @Test
  void shouldMarkRejectedLinesAsDataExecutionError() {
    // Given
    BulkImportUpdateContainer<?> item = container(2, "de:05770:1282");
    when(servicePointBulkImportClient.bulkImportGlobalIdUpdate(anyList()))
        .thenReturn(List.of(withStatus(2, 400)));

    // When
    writer.accept(List.of(item));

    // Then
    assertThat(item.getBulkImportLogEntry().getStatus()).isEqualTo(BulkImportStatus.DATA_EXECUTION_ERROR);
  }
}
