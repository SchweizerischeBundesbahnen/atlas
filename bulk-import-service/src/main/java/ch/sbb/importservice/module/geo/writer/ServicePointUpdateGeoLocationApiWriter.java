package ch.sbb.importservice.module.geo.writer;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.module.geo.entity.GeoUpdateProcessItem;
import ch.sbb.importservice.module.geo.repository.GeoUpdateProcessItemRepository;
import ch.sbb.importservice.module.geo.service.ServicePointUpdateGeoLocationService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ServicePointUpdateGeoLocationApiWriter implements ChunkProcessor<ServicePointSwissWithGeoLocationModel> {

  private final GeoUpdateProcessItemRepository geoUpdateProcessItemRepository;
  private final ServicePointUpdateGeoLocationService sePoDiClientService;

  @Override
  public void process(Chunk<ServicePointSwissWithGeoLocationModel> servicePointSwissWithGeoModels,
      @NonNull StepContribution contribution) {
    List<ServicePointSwissWithGeoLocationModel> servicePointSwissWithGeoLocationModels =
        new ArrayList<>(servicePointSwissWithGeoModels.getItems());
    doWrite(servicePointSwissWithGeoLocationModels, contribution);

    contribution.incrementWriteCount(servicePointSwissWithGeoLocationModels.size());
  }

  void doWrite(List<ServicePointSwissWithGeoLocationModel> servicePointSwissWithGeoLocationModels,
      StepContribution contribution) {
    servicePointSwissWithGeoLocationModels.forEach(swissWithGeoModel -> swissWithGeoModel.getDetails()
        .forEach(detail -> {
          GeoUpdateItemResultModel result =
              sePoDiClientService.updateServicePointGeoLocation(swissWithGeoModel.getSloid(), detail.getId());
          log.info("Process ServicePoint [sloid={},id={}] with GeoLocation...", swissWithGeoModel.getSloid(),
              detail.getId());
          if (result != null) {
            GeoUpdateProcessItem geoUpdateProcessItem = getGeoUpdateProcessItem(result, contribution);
            geoUpdateProcessItemRepository.saveAndFlush(geoUpdateProcessItem);
            log.info("Result: {}", result);
          } else {
            log.info("No GeoLocation updated!");
          }
        }));
  }

  private GeoUpdateProcessItem getGeoUpdateProcessItem(GeoUpdateItemResultModel result, StepContribution contribution) {
    return GeoUpdateProcessItem.builder()
        .sloid(result.getSloid())
        .servicePointId(result.getId())
        .jobExecutionName(contribution.getStepExecution().getJobExecution().getJobInstance().getJobName())
        .stepExecutionId(contribution.getStepExecution().getId())
        .responseStatus(result.getStatus())
        .responseMessage(result.getMessage())
        .build();
  }

}
