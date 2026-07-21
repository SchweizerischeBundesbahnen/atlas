package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.ServicePointGeoDataService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.CreateServicePointMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.GlobalIdService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
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
public class ServicePointApiV1Controller implements ServicePointApiV1 {

  private final ServicePointService servicePointService;
  private final ServicePointGeoDataService servicePointGeoDataService;
  private final CreateServicePointMapper createServicePointMapper;
  private final GlobalIdService globalIdService;

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
        .objects(globalIdService.enrich(
            servicePointVersions.stream().map(ServicePointVersionMapper::toModel).toList()))
        .totalCount(servicePointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadServicePointVersionModel> getServicePointVersions(Integer servicePointNumber) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    List<ReadServicePointVersionModel> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
            number).stream()
        .map(ServicePointVersionMapper::toModel).toList();
    if (servicePointVersions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }
    return globalIdService.enrich(servicePointVersions);
  }

  @Override
  public List<ReadServicePointVersionModel> getServicePointVersionsBySloid(String sloid) {
    return globalIdService.enrich(servicePointService.findBySloidAndOrderByValidFrom(sloid)
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList());
  }

  @Override
  public ReadServicePointVersionModel getServicePointVersion(Long id) {
    return servicePointService.findById(id).map(ServicePointVersionMapper::toModel).map(globalIdService::enrich)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public ReadServicePointVersionModel createServicePoint(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointVersion servicePointVersion = createServicePointMapper.toEntity(createServicePointVersionModel);

    if (servicePointVersion.hasGeolocation()) {
      servicePointVersion.setServicePointGeolocation(
          servicePointGeoDataService.getGeoReferenceInformation(servicePointVersion.getServicePointGeolocation()));
    }

    ServicePointVersion createdVersion = servicePointService.createAndPublish(servicePointVersion, Optional.empty(), List.of());
    return globalIdService.enrich(ServicePointVersionMapper.toModel(createdVersion));
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id,
      UpdateServicePointVersionModel updateServicePointVersionModel) {
    ServicePointVersion servicePointVersionToUpdate = servicePointService.getServicePointVersionById(id);

    List<ServicePointVersion> currentVersions = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersionToUpdate.getNumber());

    ServicePointVersion editedVersion = ServicePointVersionMapper.toEntity(updateServicePointVersionModel,
        servicePointVersionToUpdate.getNumber());

    if (editedVersion.hasGeolocation()) {
      editedVersion.setServicePointGeolocation(
          servicePointGeoDataService.getGeoReferenceInformation(editedVersion.getServicePointGeolocation()));
    }

    List<ReadServicePointVersionModel> updatedVersions = servicePointService.updateAndPublish(servicePointVersionToUpdate,
        editedVersion, currentVersions);
    return globalIdService.enrich(updatedVersions);
  }

  @Override
  public ReadServicePointVersionModel terminateServicePoint(Long id, TerminateServicePointModel terminateServicePointModel) {
    return servicePointService.terminateServicePoint(id, terminateServicePointModel);
  }

}