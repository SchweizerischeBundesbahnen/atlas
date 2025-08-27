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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@StepScope
@RequiredArgsConstructor
public class TrafficPointCreateWriter extends TrafficPointCreate implements BulkImportItemWriter {

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  private final TrafficPointBulkImportClient trafficPointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<TrafficPointCreateCsvModel>> createContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);
    WriterUtil.addInNameOfTo(stepExecution, createContainers);

    log.info("Writing {} containers to service-point-directory", createContainers.size());

    List<BulkImportItemExecutionResult> importResult = trafficPointBulkImportClient.bulkImportCreate(createContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, createContainers);
  }

}
