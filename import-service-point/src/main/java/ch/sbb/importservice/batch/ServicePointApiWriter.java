package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import feign.Response;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServicePointApiWriter extends BaseApiWriter implements ItemWriter<ServicePointCsvModel> {

  @Override
  public void write(List<? extends ServicePointCsvModel> servicePoints) {
    List<ServicePointCsvModel> servicePointCsvModels = new ArrayList<>(servicePoints);
    ServicePointImportReqModel servicePointImportReqModel = new ServicePointImportReqModel();
    servicePointImportReqModel.setServicePointCsvModels(servicePointCsvModels);
    Long stepExecutionId = stepExecution.getId();
    Response response = sePoDiClientService.getServicePoints(servicePointImportReqModel);

    for (ServicePointCsvModel servicePoint : servicePoints) {
      saveItemProcessed(stepExecutionId, servicePoint.getNummer(), response);
      int status = response.status();
      log.info("Response status {}", status);
    }
  }
}
