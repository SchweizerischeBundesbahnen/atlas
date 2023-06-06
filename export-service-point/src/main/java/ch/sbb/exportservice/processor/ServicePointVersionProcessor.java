package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.ServicePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ServicePointVersionProcessor implements ItemProcessor<ServicePointVersion, ServicePointVersion> {

  @Override
  public ServicePointVersion process(ServicePointVersion version) {
    return version;
  }
}
