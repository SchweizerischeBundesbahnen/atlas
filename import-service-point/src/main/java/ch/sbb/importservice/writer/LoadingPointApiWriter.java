package ch.sbb.importservice.writer;

import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadingPointApiWriter extends BaseApiWriter implements ItemWriter<LoadingPointCsvModel> {

  @Override
  public void write(Chunk<? extends LoadingPointCsvModel> loadingPointCsvModels) {
    log.info("Call for LoadingPointService not configured...");
    log.info("Prepared {} loadingPointCsvModels to send to LoadingPointService", loadingPointCsvModels.size());
  }

}
