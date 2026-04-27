package ch.sbb.importservice.module.bulkimport.job.prm.platform.update.reduced;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.PlatformBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformReducedUpdateWriter extends PlatformReducedUpdate implements BulkImportItemWriter {

  private final PlatformBulkImportClient platformBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> updateContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);

    log.info("Writing {} containers to prm", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = platformBulkImportClient.bulkImportPlatformReducedUpdate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
