package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import feign.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServicePointApiWriter extends BaseApiWriter implements ItemWriter<ServicePointCsvModel> {

  @Override
  public void write(List<? extends ServicePointCsvModel> servicePoints) {
    Long stepExecutionId = stepExecution.getId();
    for (ServicePointCsvModel servicePoint : servicePoints) {
      Response response = sePoDiClientService.getServicePoints(servicePoint.getDidokCode());
      saveItemProcessed(stepExecutionId, servicePoint.getNummer(), response);
      int status = response.status();
      log.info("Response status {}", status);
    }
  }
}
