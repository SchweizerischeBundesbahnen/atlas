package ch.sbb.importservice.module.bulkimport.job.prm.stoppoint.terminate;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.StopPointBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StopPointTerminateWriter extends StopPointTerminate implements BulkImportItemWriter {

  private final StopPointBulkImportClient stopPointBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<SloidTerminateCsvModel>> updateContainers =
        WriterUtil.getContainers(items);

    log.info("Writing {} containers to prm", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = stopPointBulkImportClient.bulkImportStopPointTerminate(
        updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
