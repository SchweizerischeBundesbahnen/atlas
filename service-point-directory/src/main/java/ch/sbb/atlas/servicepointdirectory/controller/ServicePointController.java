package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.abbreviationsallowlist.ServicePointAbbreviationAllowList;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidAbbreviationException;
import ch.sbb.atlas.servicepointdirectory.exception.AbbreviationUpdateNotAllowedException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointFotCommentMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    if (searchRequest == null || searchRequest.getValue() == null || searchRequest.getValue().length() < 2) {
      throw new BadRequestException("You must enter at least 2 digits to start a search!");
    }
    return servicePointService.searchServicePointVersion(searchRequest.getValue());
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
  public ReadServicePointVersionModel getServicePointVersion(Long id) {
    return servicePointService.findById(id).map(ServicePointVersionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<ItemImportResult> importServicePoints(ServicePointImportRequestModel servicePointImportRequestModel) {
    return servicePointImportService.importServicePoints(servicePointImportRequestModel.getServicePointCsvModelContainers());
  }

  @Override
  public ReadServicePointVersionModel createServicePoint(CreateServicePointVersionModel createServicePointVersionModel) {
    ServicePointVersion servicePointVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);
    if (servicePointService.isServicePointNumberExisting(servicePointVersion.getNumber())) {
      throw new ServicePointNumberAlreadyExistsException(servicePointVersion.getNumber());
    }

    validateAndSetAbbreviationForCreate(servicePointVersion, createServicePointVersionModel.getAbbreviation());

    addGeoReferenceInformation(servicePointVersion);
    ServicePointVersion createdVersion = servicePointService.save(servicePointVersion);
    servicePointDistributor.publishServicePointsWithNumbers(createdVersion.getNumber());
    return ServicePointVersionMapper.toModel(createdVersion);
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id,
      CreateServicePointVersionModel createServicePointVersionModel) {

    ServicePointVersion servicePointVersionToUpdate = servicePointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    ServicePointVersion editedVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    validateAndSetAbbreviationForUpdate(servicePointVersionToUpdate,  editedVersion, createServicePointVersionModel.getAbbreviation());

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
      GeoReference geoReference = geoReferenceService.getGeoReference(servicePointGeolocation.asCoordinatePair());

      servicePointGeolocation.setCountry(geoReference.getCountry());
      servicePointGeolocation.setSwissCanton(geoReference.getSwissCanton());
      servicePointGeolocation.setSwissDistrictNumber(geoReference.getSwissDistrictNumber());
      servicePointGeolocation.setSwissDistrictName(geoReference.getSwissDistrictName());
      servicePointGeolocation.setSwissMunicipalityNumber(geoReference.getSwissMunicipalityNumber());
      servicePointGeolocation.setSwissMunicipalityName(geoReference.getSwissMunicipalityName());
      servicePointGeolocation.setSwissLocalityName(geoReference.getSwissLocalityName());
    }
  }
  public void validateAndSetAbbreviationForCreate(ServicePointVersion servicePointVersion, String abbreviation) {
    if (StringUtils.isBlank(abbreviation)) {
      return;
    }
    commonAbbreviationValidations(servicePointVersion, abbreviation);
    servicePointVersion.setAbbreviation(abbreviation);
  }

  public void validateAndSetAbbreviationForUpdate(ServicePointVersion existingServicePointVersion, ServicePointVersion editedVersion, String newAbbreviation) {
    String existingAbbreviation = existingServicePointVersion.getAbbreviation();


    if (StringUtils.isBlank(newAbbreviation) && StringUtils.isBlank(existingAbbreviation)) {
      return;
    }

    if(servicePointService.hasServicePointVersionAbbreviation(existingServicePointVersion, newAbbreviation)){
      throw new AbbreviationUpdateNotAllowedException();
    }

    if(servicePointService.isHighDateVersion(editedVersion)) {
      throw new InvalidAbbreviationException();
    }

    commonAbbreviationValidations(editedVersion, newAbbreviation);
    editedVersion.setAbbreviation(newAbbreviation);
  }

  public void commonAbbreviationValidations(ServicePointVersion servicePointVersion, String abbreviation) {
    boolean isBussinesOrganisationInList = ServicePointAbbreviationAllowList.SBOIDS.contains(servicePointVersion.getBusinessOrganisation());
    if(!isBussinesOrganisationInList) {
      throw new AbbreviationUpdateNotAllowedException();
    }

    if(!servicePointService.isAbbrevitionUnique(abbreviation, servicePointVersion.getNumber())) {
      throw new InvalidAbbreviationException();
    }
  }
}
