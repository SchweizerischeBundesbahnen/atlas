package ch.sbb.atlas.imports.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BaseBulkImportControllerTest {

  @Autowired
  private AtlasExceptionHandler atlasExceptionHandler;

  private BaseBulkImportController bulkImportController;

  @BeforeEach
  void setUp() {
    bulkImportController = new BaseBulkImportController(atlasExceptionHandler);
  }

  @Test
  void shouldUpdateWithInNameOf() {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> containers = List.of(
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .inNameOf("user")
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .number(8507000)
                .build())
            .build());

    BiConsumer<String, BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateByUser = mock();
    Consumer<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> update = mock();

    bulkImportController.executeBulkImport(containers, updateByUser, update);

    verify(updateByUser).accept(eq("user"), any());
    verifyNoInteractions(update);
  }

  @Test
  void shouldSuccessfullyUpdateWithCurrentUser() {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> containers = List.of(
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(1)
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .number(8507000)
                .build())
            .build());

    BiConsumer<String, BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateByUser = mock();
    Consumer<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> update = mock();

    List<BulkImportItemExecutionResult> results = bulkImportController.executeBulkImport(containers,
        updateByUser, update);

    verifyNoInteractions(updateByUser);
    verify(update).accept(any());

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().isSuccess()).isTrue();
  }

  @Test
  void shouldReportErrorResponseOnUpdateWithCurrentUser() {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> containers = List.of(
        BulkImportUpdateContainer.<ServicePointUpdateCsvModel>builder()
            .lineNumber(1)
            .object(ServicePointUpdateCsvModel.builder()
                .sloid("ch:1:sloid:7000")
                .number(8507000)
                .build())
            .build());

    BiConsumer<String, BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateByUser = mock();
    Consumer<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> update = mock();

    doThrow(new SloidNotFoundException("ch:1:sloid:7000")).when(update).accept(any());

    List<BulkImportItemExecutionResult> results = bulkImportController.executeBulkImport(containers,
        updateByUser, update);

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().getErrorResponse()).isNotNull();
  }
}