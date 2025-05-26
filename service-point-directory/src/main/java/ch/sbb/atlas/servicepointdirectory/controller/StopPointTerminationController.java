package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.servicepointdirectory.api.StopPointTerminationApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationDateException;
import ch.sbb.atlas.servicepointdirectory.helper.TerminationHelper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointTerminationController implements StopPointTerminationApiV1 {

  private final ServicePointService servicePointService;
  private final TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;

  @Override
  public ReadServicePointVersionModel startServicePointTermination(String sloid, Long id,
      UpdateTerminationServicePointModel updateTerminationServicePointModel) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    ServicePointVersion servicePointVersion = TerminationHelper.validateStopPointTermination(sloid, id, servicePointVersions);

    if (updateTerminationServicePointModel.getTerminationDate().isAfter(servicePointVersion.getValidTo())
        || updateTerminationServicePointModel.getTerminationDate().isEqual(servicePointVersion.getValidTo())) {
      throw new TerminationDateException(updateTerminationServicePointModel.getTerminationDate(),
          servicePointVersion.getValidTo());
    }
    return ServicePointVersionMapper.toModel(
        servicePointService.updateStopPointTerminationStatus(servicePointVersion, servicePointVersions,
            updateTerminationServicePointModel));
  }

  @Override
  public ReadServicePointVersionModel stopServicePointTermination(String sloid, Long id) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    ServicePointVersion servicePointVersion = TerminationHelper.validateStopPointTermination(sloid, id, servicePointVersions);
    UpdateTerminationServicePointModel terminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(false)
        .build();
    return ServicePointVersionMapper.toModel(
        servicePointService.updateStopPointTerminationStatus(servicePointVersion, servicePointVersions,
            terminationServicePointModel));
  }

}
