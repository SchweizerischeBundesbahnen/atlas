package ch.sbb.importservice.batch;

import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import feign.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadingPointApiWriter extends BaseApiWriter implements ItemWriter<LoadingPointCsvModel> {

  @Override
  public void write(List<? extends LoadingPointCsvModel> loadingPointCsvModels) {
    Long stepExecutionId = stepExecution.getId();
    for (LoadingPointCsvModel loadingPointCsvModel : loadingPointCsvModels) {
      Response response = sePoDiClientService.getServicePoints(loadingPointCsvModel.getNumber());
      saveItemProcessed(stepExecutionId, loadingPointCsvModel.getNumber(), response);
      int status = response.status();
      log.info("Response status {}", status);
    }
  }

}
