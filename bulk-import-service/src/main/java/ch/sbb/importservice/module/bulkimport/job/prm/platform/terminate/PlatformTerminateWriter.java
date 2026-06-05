package ch.sbb.importservice.module.bulkimport.job.prm.platform.terminate;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.PlatformTerminateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.PlatformBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformTerminateWriter extends PlatformTerminate implements BulkImportItemWriter {

  private final PlatformBulkImportClient platformBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<PlatformTerminateCsvModel>> updateContainers =
        WriterUtil.getContainers(items);

    log.info("Writing {} containers to prm", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = platformBulkImportClient.bulkImportTerminate(
        updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
