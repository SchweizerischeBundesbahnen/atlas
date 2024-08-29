package ch.sbb.importservice.writer;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.imports.ItemImportResult;
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
public class ServicePointUpdateGeoApiWriter extends BaseApiWriter implements ItemWriter<ServicePointSwissWithGeoModel> {

  @Override
  public void write(Chunk<? extends ServicePointSwissWithGeoModel> servicePoints) {
    List<ServicePointSwissWithGeoModel> servicePointCsvModels = new ArrayList<>(servicePoints.getItems());
    //TODO: create request model
//    ServicePointImportRequestModel servicePointImportRequestModel = new ServicePointImportRequestModel();
//    servicePointImportRequestModel.setServicePointCsvModelContainers(servicePointCsvModels);
    Long stepExecutionId = stepExecution.getId();
    List<ItemImportResult> servicePointsResult = sePoDiClientService.postServicePoints("todo model");

    for (ItemImportResult response : servicePointsResult) {
      saveItemProcessed(stepExecutionId, response.getItemNumber().toString(), response.getStatus(), response.getMessage());
    }
  }
}
