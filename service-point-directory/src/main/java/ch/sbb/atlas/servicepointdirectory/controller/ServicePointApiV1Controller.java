package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.CreateServicePointMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
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
  private final GeoReferenceService geoReferenceService;
  private final CreateServicePointMapper createServicePointMapper;

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
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    List<ReadServicePointVersionModel> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(
            number).stream()
        .map(ServicePointVersionMapper::toModel).toList();
    if (servicePointVersions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(number);
    }
    return servicePointVersions;
  }

  @Override
  public List<ReadServicePointVersionModel> getServicePointVersionsBySloid(String sloid) {
    return servicePointService.findBySloidAndOrderByValidFrom(sloid)
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
  }

  @Override
  public ReadServicePointVersionModel getServicePointVersion(Long id) {
    return servicePointService.findById(id).map(ServicePointVersionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public ReadServicePointVersionModel createServicePoint(
      CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointVersion servicePointVersion = createServicePointMapper.toEntity(createServicePointVersionModel);
    geoReferenceService.addGeoReferenceInformation(servicePointVersion);
    ServicePointVersion createdVersion = servicePointService.createAndPublish(servicePointVersion, Optional.empty(), List.of());
    return ServicePointVersionMapper.toModel(createdVersion);
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id,
      UpdateServicePointVersionModel updateServicePointVersionModel) {
    ServicePointVersion servicePointVersionToUpdate = servicePointService.getServicePointVersionById(id);

    List<ServicePointVersion> currentVersions = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersionToUpdate.getNumber());

    ServicePointVersion editedVersion = ServicePointVersionMapper.toEntity(updateServicePointVersionModel,
        servicePointVersionToUpdate.getNumber());

    geoReferenceService.addGeoReferenceInformation(editedVersion);

    return servicePointService.updateAndPublish(servicePointVersionToUpdate, editedVersion, currentVersions);
  }

  @Override
  public ReadServicePointVersionModel terminateServicePoint(Long id, TerminateServicePointModel terminateServicePointModel) {
    return servicePointService.terminateServicePoint(id, terminateServicePointModel);
  }

}
