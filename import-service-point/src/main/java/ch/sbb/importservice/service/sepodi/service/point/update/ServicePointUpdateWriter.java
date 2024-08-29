package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.service.bulk.writer.BulkImportItemWriter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointUpdateWriter extends ServicePointUpdate implements BulkImportItemWriter {

  private final ServicePointBulkImportClient servicePointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateContainers =
        items.getItems().stream()
            .map(i -> (BulkImportUpdateContainer<ServicePointUpdateCsvModel>)i)
            .filter(i -> !i.hasDataValidationErrors())
            .toList();

    log.info("Writing {} containers to service-point-directory", updateContainers.size());
    List<BulkImportItemExecutionResult> importResult = servicePointBulkImportClient.bulkImportUpdate(updateContainers);

    updateContainers.forEach(updateContainer -> {
      BulkImportItemExecutionResult correspondingResult = importResult.stream()
          .filter(i -> i.getLineNumber() == updateContainer.getLineNumber()).findFirst().orElseThrow();
      updateContainer.setBulkImportLogEntry(BulkImportLogEntry.builder()
              .lineNumber(updateContainer.getLineNumber())
              .status(correspondingResult.isSuccess() ? BulkImportStatus.SUCCESS : BulkImportStatus.DATA_EXECUTION_ERROR)
              .errors(correspondingResult.getErrors())
          .build());
    });
  }
}
