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
import org.springframework.batch.core.ExitStatus;
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
    doWrite(new ArrayList<>(servicePointSwissWithGeoModels.getItems()), contribution);
  }

  void doWrite(List<ServicePointSwissWithGeoLocationModel> servicePointSwissWithGeoLocationModels,
      StepContribution contribution) {
    servicePointSwissWithGeoLocationModels
        .forEach(swissWithGeoModel -> swissWithGeoModel.getDetails()
            .forEach(detail -> doWrite(swissWithGeoModel, detail, contribution)));
  }

  void doWrite(ServicePointSwissWithGeoLocationModel servicePoint,
      ServicePointSwissWithGeoLocationModel.Detail versionInfo,
      StepContribution contribution) {
    try {
      GeoUpdateItemResultModel result = sePoDiClientService.updateServicePointGeoLocation(servicePoint.getSloid(),
          versionInfo.getId());
      log.info("Process ServicePoint [sloid={},id={}] with GeoLocation...", servicePoint.getSloid(),
          versionInfo.getId());

      contribution.incrementWriteCount(1);
      if (result != null) {
        GeoUpdateProcessItem geoUpdateProcessItem = getGeoUpdateProcessItem(result, contribution);
        geoUpdateProcessItemRepository.saveAndFlush(geoUpdateProcessItem);
        log.info("Result: {}", result);
      } else {
        log.info("No GeoLocation updated!");
      }
    } catch (Exception e) {
      log.error("Error while updating GeoLocation for ServicePoint [sloid={},id={}]", servicePoint.getSloid(),
          versionInfo.getId(), e);
      contribution.incrementWriteSkipCount(1);
      contribution.setExitStatus(ExitStatus.FAILED.setExitException(e));
    }
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
