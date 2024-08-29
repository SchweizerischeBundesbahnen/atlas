package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.client.ServicePointClient;
import ch.sbb.importservice.service.bulk.writer.BulkImportItemWriter;
import ch.sbb.importservice.service.bulk.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointUpdateWriter extends ServicePointUpdate implements BulkImportItemWriter {

  private final ServicePointClient servicePointClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);

    log.info("Writing {} containers to service-point-directory", updateContainers.size());
    List<BulkImportItemExecutionResult> importResult = servicePointClient.bulkImportUpdate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }
}
