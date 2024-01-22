package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointStatusChangeNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointFotCommentMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.LocationService;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
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
public class ServicePointController implements ServicePointApiV1 {

  private final ServicePointService servicePointService;
  private final ServicePointFotCommentService servicePointFotCommentService;
  private final ServicePointImportService servicePointImportService;
  private final GeoReferenceService geoReferenceService;
  private final ServicePointDistributor servicePointDistributor;
  private final LocationService locationService;

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
  public List<ServicePointSearchResult> searchServicePoints(ServicePointSearchRequest searchRequest) {
    return servicePointService.searchServicePointVersion(searchRequest.getValue());
  }

  @Override
  public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(ServicePointSearchRequest searchRequest) {
    return servicePointService.searchServicePointsWithRouteNetworkTrue(searchRequest.getValue());
  }

  @Override
  public List<ServicePointSearchResult> searchSwissOnlyServicePoints(ServicePointSearchRequest searchRequest) {
    return servicePointService.searchSwissOnlyServicePointVersion(searchRequest.getValue());
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
  public List<ItemImportResult> importServicePoints(ServicePointImportRequestModel servicePointImportRequestModel) {
    return servicePointImportService.importServicePoints(servicePointImportRequestModel.getServicePointCsvModelContainers());
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
    ServicePointVersion servicePointVersion;

    if (createServicePointVersionModel.shouldGenerateServicePointNumber()) {
      //case 85,11-14
      String generatedSloid = locationService.generateSloid(SloidType.SERVICE_POINT, createServicePointVersionModel.getCountry());
      log.info("Generated new SLOID={}", generatedSloid);
      ServicePointNumber servicePointNumber = SloidHelper.getServicePointNumber(generatedSloid);
      log.info("Generated new service point number={}", servicePointNumber);
      servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel, servicePointNumber);
    } else {
      //case not swiss
      ServicePointNumber manualServicePointNumber = ServicePointNumber.of(createServicePointVersionModel.getCountry(),
          createServicePointVersionModel.getNumberShort());
      if (servicePointService.isServicePointNumberExisting(manualServicePointNumber)) {
        throw new ServicePointNumberAlreadyExistsException(manualServicePointNumber);
      }
      servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel, manualServicePointNumber);
    }
    addGeoReferenceInformation(servicePointVersion);
    setCreationDateAndCreatorToNull(servicePointVersion);
    ServicePointVersion createdVersion = servicePointService.create(servicePointVersion, Optional.empty(), List.of());
    servicePointDistributor.publishServicePointsWithNumbers(createdVersion.getNumber());
    return ServicePointVersionMapper.toModel(createdVersion);
  }

  /**
   * @deprecated Only necessary as long as we use BaseDidokImportEntity
   */
  @Deprecated(forRemoval = false)
  private static void setCreationDateAndCreatorToNull(ServicePointVersion servicePointVersion) {
    servicePointVersion.setCreator(null);
    servicePointVersion.setCreationDate(null);
  }

  @Override
  public ReadServicePointVersionModel validateServicePoint(Long id) {
    ServicePointVersion servicePointVersion = servicePointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    if (!Status.DRAFT.equals(servicePointVersion.getStatus())) {
      throw new ServicePointStatusChangeNotAllowedException(servicePointVersion.getNumber(), servicePointVersion.getStatus());
    }

    ServicePointVersion validatedServicePointVersion = servicePointService.validate(servicePointVersion);

    return ServicePointVersionMapper.toModel(validatedServicePointVersion);
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id,
      UpdateServicePointVersionModel updateServicePointVersionModel) {
    ServicePointVersion servicePointVersionToUpdate = servicePointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    ServicePointVersion editedVersion = ServicePointVersionMapper.toEntity(updateServicePointVersionModel,
        servicePointVersionToUpdate.getNumber());

    addGeoReferenceInformation(editedVersion);

    servicePointService.update(servicePointVersionToUpdate, editedVersion,
        servicePointService.findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber()));

    List<ServicePointVersion> servicePoint = servicePointService.findAllByNumberOrderByValidFrom(
        servicePointVersionToUpdate.getNumber());
    servicePointDistributor.publishServicePointsWithNumbers(servicePointVersionToUpdate.getNumber());
    return servicePoint
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
  }

  @Override
  public Optional<ServicePointFotCommentModel> getFotComment(Integer servicePointNumber) {
    return servicePointFotCommentService.findByServicePointNumber(servicePointNumber).map(ServicePointFotCommentMapper::toModel);
  }

  @Override
  public ServicePointFotCommentModel saveFotComment(Integer servicePointNumber, ServicePointFotCommentModel fotComment) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    if (!servicePointService.isServicePointNumberExisting(number)) {
      throw new ServicePointNumberNotFoundException(number);
    }

    ServicePointFotComment entity = ServicePointFotCommentMapper.toEntity(fotComment, number);
    return ServicePointFotCommentMapper.toModel(servicePointFotCommentService.save(entity));
  }

  @Override
  public void syncServicePoints() {
    log.info("Syncing all Service Points");
    servicePointDistributor.syncServicePoints();
  }

  private void addGeoReferenceInformation(ServicePointVersion servicePointVersion) {
    if (servicePointVersion.hasGeolocation()) {
      ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
      GeoReference geoReference = geoReferenceService.getGeoReference(servicePointGeolocation.asCoordinatePair(),
          servicePointGeolocation.getHeight() == null);

      if (geoReference.getHeight() != null) {
        servicePointGeolocation.setHeight(geoReference.getHeight());
      }

      servicePointGeolocation.setCountry(geoReference.getCountry());
      servicePointGeolocation.setSwissCanton(geoReference.getSwissCanton());
      servicePointGeolocation.setSwissDistrictNumber(geoReference.getSwissDistrictNumber());
      servicePointGeolocation.setSwissDistrictName(geoReference.getSwissDistrictName());
      servicePointGeolocation.setSwissMunicipalityNumber(geoReference.getSwissMunicipalityNumber());
      servicePointGeolocation.setSwissMunicipalityName(geoReference.getSwissMunicipalityName());
      servicePointGeolocation.setSwissLocalityName(geoReference.getSwissLocalityName());
    }
  }

}
