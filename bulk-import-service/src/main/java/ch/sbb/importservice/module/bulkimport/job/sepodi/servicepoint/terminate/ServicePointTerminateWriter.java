package ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.terminate;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.importservice.module.bulkimport.client.ServicePointBulkImportClient;
import ch.sbb.importservice.module.bulkimport.writer.BulkImportItemWriter;
import ch.sbb.importservice.module.bulkimport.writer.WriterUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointTerminateWriter extends ServicePointTerminate implements BulkImportItemWriter {

  private final ServicePointBulkImportClient servicePointBulkImportClient;

  @Override
  public void accept(List<BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<ServicePointTerminateCsvModel>> updateContainers =
        WriterUtil.getContainers(items);

    log.info("Writing {} containers to service-point-directory", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = servicePointBulkImportClient.bulkImportTerminate(updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }

}
