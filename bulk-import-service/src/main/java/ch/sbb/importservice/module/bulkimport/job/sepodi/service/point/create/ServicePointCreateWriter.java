package ch.sbb.importservice.module.bulkimport.job.sepodi.service.point.create;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.ServicePointBulkImportClient;
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
public class ServicePointCreateWriter extends ServicePointCreate implements BulkImportItemWriter {

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  private final ServicePointBulkImportClient servicePointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    log.info("Writing {} items", items.size());

    List<BulkImportUpdateContainer<ServicePointCreateCsvModel>> updateContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);
    WriterUtil.addInNameOfTo(stepExecution, updateContainers);

    log.info("Writing {} containers to service-point-directory", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = servicePointBulkImportClient.bulkImportCreate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }
}
