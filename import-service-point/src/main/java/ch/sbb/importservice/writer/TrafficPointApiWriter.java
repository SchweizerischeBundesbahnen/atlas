package ch.sbb.importservice.writer;

import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrafficPointApiWriter extends BaseApiWriter implements ItemWriter<TrafficPointElementCsvModel> {

  @Override
  public void write(Chunk<? extends TrafficPointElementCsvModel> trafficPointCsvModels) {
    // TODO: implement
    log.info("Prepared {} trafficPointCsvModels to send to ServicePointDirectory", trafficPointCsvModels.size());
  }

}
