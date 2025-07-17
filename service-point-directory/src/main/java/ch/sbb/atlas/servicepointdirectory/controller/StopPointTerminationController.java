package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.StopPointTerminationApiInternal;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationDateException;
import ch.sbb.atlas.servicepointdirectory.helper.ServicePointTerminationHelper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointTerminationController implements StopPointTerminationApiInternal {

  private final ServicePointService servicePointService;
  private final TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;

  @Override
  public ReadServicePointVersionModel startServicePointTermination(String sloid, Long id,
      UpdateTerminationServicePointModel updateTerminationServicePointModel) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    ServicePointVersion servicePointVersion = ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(sloid, id,
        servicePointVersions);

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
    if (servicePointVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    ServicePointVersion servicePointVersion = servicePointVersions.stream().filter(sp -> sp.getId().equals(id)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(id));
    UpdateTerminationServicePointModel terminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(false)
        .build();
    return ServicePointVersionMapper.toModel(
        servicePointService.updateStopPointTerminationStatus(servicePointVersion, servicePointVersions,
            terminationServicePointModel));
  }

  @Override
  public void terminateStopPoint(Long id, LocalDate date) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();

    ServicePointVersion currentVersion = servicePointService.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    stopServicePointTermination(currentVersion.getSloid(), currentVersion.getId());

    ServicePointVersion editedVersion = servicePointService.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    editedVersion.setValidTo(date);
    servicePointService.updateAndPublish(currentVersion, editedVersion,
        servicePointService.findAllByNumberOrderByValidFrom(currentVersion.getNumber()));
  }

  @Override
  public void changeToTariffStop(Long id, LocalDate date) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();

    ServicePointVersion currentVersion = servicePointService.findById(id).orElseThrow(() -> new IdNotFoundException(id));

    ServicePointVersion editedVersion = servicePointService.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    editedVersion.setValidFrom(date);
    editedVersion.setMeansOfTransport(Collections.emptySet());
    editedVersion.setStopPointType(null);
    editedVersion.setServicePointGeolocation(null);
    editedVersion.setFreightServicePoint(false);
    editedVersion.setBusinessOrganisation(ServicePointConstants.ALLIANCE_SWISS_PASS_SBOID);

    servicePointService.updateAndPublish(currentVersion, editedVersion,
        servicePointService.findAllByNumberOrderByValidFrom(currentVersion.getNumber()));
  }

}
