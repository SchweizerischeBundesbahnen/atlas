package ch.sbb.importservice.module.bulkimport.job.lidi.line.create;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.LineBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineCreateWriter extends LineCreate implements BulkImportItemWriter {

  private final LineBulkImportClient lineBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<LineCreateCsvModel>> updateContainers = WriterUtil.getContainers(items);

    log.info("Writing {} containers to lidi", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = lineBulkImportClient.lineCreate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
