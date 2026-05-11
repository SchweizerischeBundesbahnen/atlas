package ch.sbb.importservice.module.bulkimport.job.lidi.subline.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.SublineBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SublineUpdateWriter extends SublineUpdate implements BulkImportItemWriter {

  private final SublineBulkImportClient sublineBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<SublineUpdateCsvModel>> updateContainers = WriterUtil.getContainers(items);

    log.info("Writing {} containers to lidi", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = sublineBulkImportClient.sublineUpdate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}

