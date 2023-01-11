package ch.sbb.importservice.batch;

import ch.sbb.importservice.model.ServicePoint;
import ch.sbb.importservice.service.SePoDiClientService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ServicePointApiWriter implements ItemWriter<ServicePoint> {

  private final SePoDiClientService sePoDiClientService;

  @Override
  public void write(List<? extends ServicePoint> servicePoints) {
    log.info("Post servicePoints to API");
    for (ServicePoint servicePoint : servicePoints) {
      log.info(servicePoint.toString());
      //      sePoDiClientService.postServicePoints();
    }
  }
}
