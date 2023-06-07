package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
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
  public Container<ReadServicePointVersionModel> getServicePoints(Pageable pageable,
      ServicePointRequestParams servicePointRequestParams) {
    log.info("Loading ServicePointVersions with pageable={} and servicePointRequestParams={}", pageable,
        servicePointRequestParams);
    ServicePointSearchRestrictions searchRestrictions = ServicePointSearchRestrictions.builder()
        .pageable(pageable)
        .servicePointRequestParams(servicePointRequestParams)
        .build();
    Page<ServicePointVersion> servicePointVersions = servicePointService.findAll(searchRestrictions);
    return Container.<ReadServicePointVersionModel>builder()
        .objects(servicePointVersions.stream().map(ServicePointVersionMapper::toModel).toList())
        .totalCount(servicePointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadServicePointVersionModel> getServicePointVersions(Integer servicePointNumber) {
    ServicePointNumber number = ServicePointNumber.of(servicePointNumber);
    List<ReadServicePointVersionModel> servicePointVersions = servicePointService.findAllServicePointVersions(
            number).stream()
        .map(ServicePointVersionMapper::toModel).toList();
    if (servicePointVersions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }
    return servicePointVersions;
  }

  @Override
  public ReadServicePointVersionModel getServicePointVersion(Long id) {
    return servicePointService.findById(id).map(ServicePointVersionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<ServicePointItemImportResult> importServicePoints(ServicePointImportReqModel servicePointImportReqModel) {
    return servicePointImportService.importServicePoints(servicePointImportReqModel.getServicePointCsvModelContainers());
  }

  @Override
  public ReadServicePointVersionModel createServicePoint(CreateServicePointVersionModel createServicePointVersionModel) {
    return ServicePointVersionMapper.toModel(servicePointService.save(ServicePointVersionMapper.toEntity(createServicePointVersionModel)));
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id, CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointVersion servicePointVersionToUpdate = servicePointService.findById(id)
            .orElseThrow(() -> new IdNotFoundException(id));
    createServicePointVersionModel.setId(id);
    servicePointService.updateServicePointVersion(ServicePointVersionMapper.toEntity(createServicePointVersionModel));
    return servicePointService.findAllServicePointVersions(servicePointVersionToUpdate.getNumber())
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
  }

}
