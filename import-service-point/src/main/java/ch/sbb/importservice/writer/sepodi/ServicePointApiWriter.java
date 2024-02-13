package ch.sbb.importservice.writer.sepodi;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class ServicePointApiWriter extends BaseApiWriter implements ItemWriter<ServicePointCsvModelContainer> {

  @Override
  public void write(Chunk<? extends ServicePointCsvModelContainer> servicePoints) {
    List<ServicePointCsvModelContainer> servicePointCsvModels = new ArrayList<>(servicePoints.getItems());
    ServicePointImportRequestModel servicePointImportRequestModel = new ServicePointImportRequestModel();
    servicePointImportRequestModel.setServicePointCsvModelContainers(servicePointCsvModels);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> servicePointsResult = sePoDiClientService.postServicePoints(
        servicePointImportRequestModel);

    for (ItemImportResult response : servicePointsResult) {
      saveItemProcessed(stepExecutionId, response.getItemNumber().toString(), response.getStatus(), response.getMessage());
    }
  }
}
