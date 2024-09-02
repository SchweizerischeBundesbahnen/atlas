package ch.sbb.importservice.writer;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.entity.ImportProcessItem;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class ServicePointUpdateGeoApiWriter extends BaseApiWriter implements ItemWriter<ServicePointSwissWithGeoModel> {

  @Override
  public void write(Chunk<? extends ServicePointSwissWithGeoModel> servicePointSwissWithGeoModels) {
    List<ServicePointSwissWithGeoModel> servicePointCsvModels = new ArrayList<>(servicePointSwissWithGeoModels.getItems());
    servicePointCsvModels.forEach(servicePointSwissWithGeoModel -> servicePointSwissWithGeoModel.getDetails().forEach(detail -> {
      GeoUpdateItemResultModel result =
          sePoDiClientService.updateServicePointGeoLocation(servicePointSwissWithGeoModel.getSloid(), detail.getId());
      log.info("Process ServicePoint [sloid={},id={}] with GeoLocation...", detail.getId(),
          servicePointSwissWithGeoModel.getSloid());
      if (result != null) {

        ImportProcessItem importProcessItem = ImportProcessItem.builder()
            .itemNumber(result.getSloid())
            .id(result.getId())
            .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
            .stepExecutionId(stepExecution.getId())
            .responseStatus(result.getStatus())
            .responseMessage(result.getMessage())
            .build();
        importProcessedItemRepository.saveAndFlush(importProcessItem);
        log.info("Resul: {}", result);
      } else {
        log.info("No GeoLocation updated!");
      }
    }));
  }
}
