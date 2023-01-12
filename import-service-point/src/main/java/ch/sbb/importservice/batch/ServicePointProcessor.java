package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.ServicePointCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointProcessor implements ItemProcessor<ServicePointCsvModel, ServicePointCsvModel> {

  @Override
  public ServicePointCsvModel process(ServicePointCsvModel servicePoint) {
    return servicePoint;
  }
}
