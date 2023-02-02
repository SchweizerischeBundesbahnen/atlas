package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.List;
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
  private final ServicePointImportService servicePointImportService;

  @Override
  public Container<ServicePointVersionModel> getServicePoints(Pageable pageable,
      ServicePointRequestParams servicePointRequestParams) {
    ServicePointSearchRestrictions searchRestrictions = ServicePointSearchRestrictions.builder()
        .pageable(pageable)
        .servicePointRequestParams(servicePointRequestParams)
        .build();
    Page<ServicePointVersion> servicePointVersions = servicePointService.findAll(searchRestrictions);
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

  @Override
  public List<ServicePointItemImportResult> importServicePoints(ServicePointImportReqModel servicePointImportReqModel) {
    return servicePointImportService.importServicePoints(servicePointImportReqModel.getServicePointCsvModelContainers());
  }

}
