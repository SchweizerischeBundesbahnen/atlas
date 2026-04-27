package ch.sbb.importservice.module.bulkimport.job.sepodi.sector.create;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.SectorBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectorCreateWriter extends SectorCreate implements BulkImportItemWriter {

  private final SectorBulkImportClient sectorBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<SectorCreateCsvModel>> createContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);

    List<BulkImportItemExecutionResult> importResult = sectorBulkImportClient.bulkImportCreate(createContainers);
    WriterUtil.mapExecutionResultToLogEntry(importResult, createContainers);
  }

}
