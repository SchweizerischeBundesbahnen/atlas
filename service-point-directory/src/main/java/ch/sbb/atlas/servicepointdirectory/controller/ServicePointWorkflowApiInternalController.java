package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointWorkflowApiInternal;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusRevokedChangeNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointWorkflowApiInternalController implements ServicePointWorkflowApiInternal {

  private final ServicePointService servicePointService;

  @Override
  public ReadServicePointVersionModel validateServicePoint(Long id) {
    ServicePointVersion servicePointVersion = servicePointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    if (!Status.DRAFT.equals(servicePointVersion.getStatus())) {
      throw new ServicePointStatusRevokedChangeNotAllowedException(servicePointVersion.getNumber(),
          servicePointVersion.getStatus());
    }

    ServicePointVersion validatedServicePointVersion = servicePointService.validate(servicePointVersion);

    return ServicePointVersionMapper.toModel(validatedServicePointVersion);
  }

  @Override
  public ReadServicePointVersionModel updateDesignationOfficial(Long id,
      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel) {

    return servicePointService.updateDesignationOfficial(id, updateDesignationOfficialServicePointModel);
  }

  @Override
  public ReadServicePointVersionModel updateServicePointStatus(String sloid, Long id, Status status) {
    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    if (servicePointVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    ServicePointVersion servicePointVersion = servicePointVersions.stream().filter(sp -> sp.getId().equals(id)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(id));

    return ServicePointVersionMapper.toModel(
        servicePointService.updateStopPointStatusForWorkflow(servicePointVersion, servicePointVersions,
            status));
  }

}
