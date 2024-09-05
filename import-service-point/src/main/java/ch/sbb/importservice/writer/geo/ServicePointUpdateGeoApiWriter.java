package ch.sbb.importservice.writer.geo;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.entity.GeoUpdateProcessItem;
import ch.sbb.importservice.repository.GeoUpdateProcessItemRepository;
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
  private GeoUpdateProcessItemRepository geoUpdateProcessItemRepository;

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
    servicePointCsvModels.forEach(swissWithGeoModel -> swissWithGeoModel.getDetails()
        .forEach(detail -> {
          GeoUpdateItemResultModel result =
              sePoDiClientService.updateServicePointGeoLocation(swissWithGeoModel.getSloid(), detail.getId());
          log.info("Process ServicePoint [sloid={},id={}] with GeoLocation...", swissWithGeoModel.getSloid(),
              detail.getId());
          if (result != null) {
            GeoUpdateProcessItem geoUpdateProcessItem = getGeoUpdateProcessItem(result);
            geoUpdateProcessItemRepository.saveAndFlush(geoUpdateProcessItem);
            log.info("Result: {}", result);
          } else {
            log.info("No GeoLocation updated!");
          }
        }));
  }

  private GeoUpdateProcessItem getGeoUpdateProcessItem(GeoUpdateItemResultModel result) {
    return GeoUpdateProcessItem.builder()
        .sloid(result.getSloid())
        .servicePointId(result.getId())
        .jobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName())
        .stepExecutionId(stepExecution.getId())
        .responseStatus(result.getStatus())
        .responseMessage(result.getMessage())
        .build();
  }
}
