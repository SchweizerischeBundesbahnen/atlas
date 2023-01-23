package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointProcessor implements ItemProcessor<ServicePointCsvModelContainer, ServicePointCsvModelContainer> {

  @Override
  public ServicePointCsvModelContainer process(ServicePointCsvModelContainer servicePoint) {
    return servicePoint;
  }
}
