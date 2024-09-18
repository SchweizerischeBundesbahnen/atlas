package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.service.bulk.writer.BulkImportItemWriter;
import ch.sbb.importservice.service.bulk.writer.WriterUtil;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@StepScope
@RequiredArgsConstructor
public class ServicePointUpdateWriter extends ServicePointUpdate implements BulkImportItemWriter {

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  private final ServicePointBulkImportClient servicePointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportUpdateContainer<?>> items) {
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateContainers =
        WriterUtil.getContainersWithoutDataValidationErrors(items);
    JobParameters jobParameters = stepExecution.getJobExecution().getJobParameters();
    Map<String, JobParameter<?>> parameters = jobParameters.getParameters();
    String inNameOf;

    if(parameters.containsKey("inNameOf")){
     inNameOf  = String.valueOf(parameters.get("inNameOf").getValue());
    }
    else{
      inNameOf = null;
    }


    log.info("Writing {} containers to service-point-directory", updateContainers.size());

    List<BulkImportItemExecutionResult> importResult = servicePointBulkImportClient.bulkImportUpdate(inNameOf, updateContainers);

    WriterUtil.mapExecutionResultToLogEntry(importResult, updateContainers);
  }
}
