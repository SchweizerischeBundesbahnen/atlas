package ch.sbb.importservice.module.bulkimport.job.lidi.line.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.LineBulkImportClient;
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
public class LineUpdateWriter extends LineUpdate implements BulkImportItemWriter {

  private final LineBulkImportClient lineBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<LineUpdateCsvModel>> updateContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);

    log.info("Writing {} containers to lidi", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = lineBulkImportClient.lineUpdate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
