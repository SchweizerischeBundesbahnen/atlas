package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointController implements ServicePointApiV1 {

  private final ServicePointService servicePointService;

  @Override
  public List<ServicePointVersionModel> getServicePointVersions(Integer servicePointNumber) {
    ServicePointNumber number = ServicePointNumber.of(servicePointNumber);
    List<ServicePointVersionModel> servicePointVersions = servicePointService.findServicePointVersions(
            number).stream()
        .map(ServicePointVersionModel::fromEntity).toList();
    if (servicePointVersions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }
    return servicePointVersions;
  }

  @Override
  public ServicePointVersionModel getServicePointVersion(Long id) {
    return servicePointService.findById(id).map(ServicePointVersionModel::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

}
