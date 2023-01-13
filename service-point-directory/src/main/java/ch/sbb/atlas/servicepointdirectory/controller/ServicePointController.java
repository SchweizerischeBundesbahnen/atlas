package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointController implements ServicePointApiV1 {

  private final ServicePointService servicePointService;

  @Override
  // TODO: add filter parameter
  public Container<ServicePointVersionModel> getServicePoints(Pageable pageable, Optional<LocalDate> validOn) {
    Page<ServicePointVersion> servicePointVersions = servicePointService.findAll(pageable);
    return Container.<ServicePointVersionModel>builder()
        .objects(servicePointVersions.stream().map(ServicePointVersionModel::fromEntity).toList())
        .totalCount(servicePointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ServicePointVersionModel> getServicePoint(Integer servicePointNumber) {
    ServicePointNumber number = ServicePointNumber.of(servicePointNumber);
    List<ServicePointVersionModel> servicePointVersions = servicePointService.findServicePoint(
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
