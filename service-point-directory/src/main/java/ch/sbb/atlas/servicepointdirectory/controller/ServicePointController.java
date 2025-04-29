package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusRevokedChangeNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationDateException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotOnLastVersionException;
import ch.sbb.atlas.servicepointdirectory.mapper.CreateServicePointMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointSwissWithGeoMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSwissWithGeoTransfer;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  public List<ReadServicePointVersionModel> revokeServicePoint(Integer servicePointNumber) {
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointService.revokeServicePoint(
            ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber))
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
    if (servicePointVersionModels.isEmpty()) {
      throw new ServicePointNumberNotFoundException(ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber));
    }
    return servicePointVersionModels;
  }

  @Override
  public ReadServicePointVersionModel createServicePoint(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointVersion servicePointVersion = createServicePointMapper.toEntity(createServicePointVersionModel);
    geoReferenceService.addGeoReferenceInformation(servicePointVersion);
    ServicePointVersion createdVersion = servicePointService.createAndPublish(servicePointVersion, Optional.empty(), List.of());
    return ServicePointVersionMapper.toModel(createdVersion);
  }

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

  @Override
  public ReadServicePointVersionModel startServicePointTermination(String sloid, Long id,
      UpdateTerminationServicePointModel updateTerminationServicePointModel) {

    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    ServicePointVersion servicePointVersion = validateTermination(sloid, id, servicePointVersions);

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
    List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(sloid);
    ServicePointVersion servicePointVersion = validateTermination(sloid, id, servicePointVersions);
    UpdateTerminationServicePointModel terminationServicePointModel = UpdateTerminationServicePointModel.builder()
        .terminationInProgress(false)
        .build();
    return ServicePointVersionMapper.toModel(
        servicePointService.updateStopPointTerminationStatus(servicePointVersion, servicePointVersions,
            terminationServicePointModel));
  }

  @Override
  public void syncServicePoints() {
    servicePointService.publishAllServicePoints();
  }

  @Override
  public List<ServicePointSwissWithGeoLocationModel> getActualServicePointWithGeolocation() {
    List<ServicePointSwissWithGeoTransfer> actualServicePointWithGeolocation =
        servicePointService.findActualServicePointWithGeolocation();

    List<ServicePointSwissWithGeoLocationModel> swissWithGeoModels = new ArrayList<>();
    actualServicePointWithGeolocation.stream()
        .collect(Collectors.groupingBy(ServicePointSwissWithGeoTransfer::getSloid))
        .forEach((sloid, swissWithGeoTransfers) ->
            swissWithGeoModels.add(ServicePointSwissWithGeoMapper.toModel(sloid, swissWithGeoTransfers)));

    return swissWithGeoModels;
  }

  private static ServicePointVersion validateTermination(String sloid, Long id,
      List<ServicePointVersion> servicePointVersions) {
    if (servicePointVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    ServicePointVersion servicePointVersion = servicePointVersions.stream().filter(sp -> sp.getId().equals(id)).findFirst()
        .orElseThrow(() -> new IdNotFoundException(id));
    if (!servicePointVersions.getLast().getId().equals(id)) {
      throw new TerminationNotOnLastVersionException();
    }
    return servicePointVersion;
  }
}
