package ch.sbb.importservice.module.bulkimport.job.sepodi.sectorgroup.create;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.SectorGroupBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectorGroupCreateWriter extends SectorGroupCreate implements BulkImportItemWriter {

  private final SectorGroupBulkImportClient sectorGroupBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<SectorGroupCreateCsvModel>> createContainers = WriterUtil.getContainers(items);

    log.info("Writing {} containers to sepodi", createContainers.size());

    List<BulkImportItemExecutionResult> importResult = sectorGroupBulkImportClient.bulkImportCreate(createContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, createContainers);
  }

}
