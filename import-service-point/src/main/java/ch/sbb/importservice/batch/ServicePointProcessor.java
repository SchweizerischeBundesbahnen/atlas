package ch.sbb.importservice.batch;

import ch.sbb.importservice.model.ServicePoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointProcessor implements ItemProcessor<ServicePoint, ServicePoint> {

  @Override
  public ServicePoint process(ServicePoint servicePoint) {
    return servicePoint;
  }
}
