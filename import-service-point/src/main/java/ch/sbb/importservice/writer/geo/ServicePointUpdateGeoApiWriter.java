package ch.sbb.importservice.writer.geo;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.entity.ImportProcessItem;
import ch.sbb.importservice.repository.ImportProcessedItemRepository;
import ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class ServicePointUpdateGeoApiWriter implements ItemWriter<ServicePointSwissWithGeoModel> {

  private StepExecution stepExecution;

  @Autowired
  private ServicePointUpdateGeoLocationService sePoDiClientService;

  @Autowired
  private ImportProcessedItemRepository importProcessedItemRepository;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  @Override
  public void write(Chunk<? extends ServicePointSwissWithGeoModel> servicePointSwissWithGeoModels) {
    List<ServicePointSwissWithGeoModel> servicePointCsvModels = new ArrayList<>(servicePointSwissWithGeoModels.getItems());
    doWrite(servicePointCsvModels);
  }

  void doWrite(List<ServicePointSwissWithGeoModel> servicePointCsvModels) {
    servicePointCsvModels.forEach(servicePointSwissWithGeoModel -> servicePointSwissWithGeoModel.getDetails().forEach(detail -> {
      GeoUpdateItemResultModel result =
          sePoDiClientService.updateServicePointGeoLocation(servicePointSwissWithGeoModel.getSloid(), detail.getId());
      log.info("Process ServicePoint [sloid={},id={}] with GeoLocation...", detail.getId(),
          servicePointSwissWithGeoModel.getSloid());
      if (result != null) {
        ImportProcessItem importProcessItem = getImportProcessItem(result);
        importProcessedItemRepository.saveAndFlush(importProcessItem);
        log.info("Result: {}", result);
      } else {
        log.info("No GeoLocation updated!");
      }
    }));
  }

  private ImportProcessItem getImportProcessItem(GeoUpdateItemResultModel result) {
    return ImportProcessItem.builder()
        .itemNumber(result.getSloid())
        .id(result.getId())
        .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
        .stepExecutionId(stepExecution.getId())
        .responseStatus(result.getStatus())
        .responseMessage(result.getMessage())
        .build();
  }
}
