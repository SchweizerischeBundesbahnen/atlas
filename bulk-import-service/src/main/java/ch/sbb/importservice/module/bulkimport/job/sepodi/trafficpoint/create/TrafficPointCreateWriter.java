package ch.sbb.importservice.module.bulkimport.job.sepodi.trafficpoint.create;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.TrafficPointBulkImportClient;
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
public class TrafficPointCreateWriter extends TrafficPointCreate implements BulkImportItemWriter {

  private final TrafficPointBulkImportClient trafficPointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<TrafficPointCreateCsvModel>> createContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);

    log.info("Writing {} containers to service-point-directory", createContainers.size());

    List<BulkImportItemExecutionResult> importResult = trafficPointBulkImportClient.bulkImportCreate(createContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, createContainers);
  }

}
