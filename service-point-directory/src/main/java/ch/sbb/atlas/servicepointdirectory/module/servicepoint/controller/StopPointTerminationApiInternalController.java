package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.helper.TerminationHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.StopPointTerminationApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationDateException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.helper.ServicePointTerminationHelper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointTerminationApiInternalController implements StopPointTerminationApiInternal {

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
  public void terminateStopPoint(StopPointWorkflowTerminationModel terminationModel) {
    ServicePointVersion lastVersion = getLastServicePointVersionCheckedForDate(terminationModel);
    ServicePointVersion editedVersion = lastVersion.toBuilder().build();
    if (terminationModel.getTerminationDate().isBefore(editedVersion.getValidTo())) {
      editedVersion.setValidTo(terminationModel.getTerminationDate());
      servicePointService.updateAndPublish(lastVersion, editedVersion,
          servicePointService.findAllByNumberOrderByValidFrom(lastVersion.getNumber()));
    }
  }

  @Override
  public void changeToTariffStop(StopPointWorkflowTerminationModel terminationModel) {
    ServicePointVersion lastVersion = getLastServicePointVersionCheckedForDate(terminationModel);

    ServicePointVersion editedVersion = lastVersion.toBuilder().build();
    editedVersion.setValidFrom(terminationModel.getTerminationDate());
    editedVersion.setMeansOfTransport(Collections.emptySet());
    editedVersion.setStopPointType(null);
    editedVersion.setServicePointGeolocation(null);
    editedVersion.setFreightServicePoint(false);
    editedVersion.setBusinessOrganisation(ServicePointConstants.ALLIANCE_SWISS_PASS_SBOID);
    editedVersion.setOperatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT);

    servicePointService.updateAndPublish(lastVersion, editedVersion,
        servicePointService.findAllByNumberOrderByValidFrom(lastVersion.getNumber()));
  }

  private ServicePointVersion getLastServicePointVersionCheckedForDate(StopPointWorkflowTerminationModel terminationModel) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();

    stopServicePointTermination(terminationModel.getSloid(), terminationModel.getVersionId());

    List<ServicePointVersion> currentVersions = servicePointService.findBySloidAndOrderByValidFrom(terminationModel.getSloid());
    ServicePointVersion lastVersion = currentVersions.getLast();
    TerminationHelper.isValidToInLastVersionRange(terminationModel.getSloid(), DateRange.fromVersionable(lastVersion),
        terminationModel.getTerminationDate());
    return lastVersion;
  }

}
