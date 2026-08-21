package ch.sbb.importservice.module.bulkimport.job.sepodi.sector.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.SectorBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectorUpdateWriter extends SectorUpdate implements BulkImportItemWriter {

  private final SectorBulkImportClient sectorBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<SectorUpdateCsvModel>> updateContainers = WriterUtil.getContainers(items);

    List<BulkImportItemExecutionResult> importResult = sectorBulkImportClient.bulkImportUpdate(updateContainers);
    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
