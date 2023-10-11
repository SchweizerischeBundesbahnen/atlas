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
import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberAlreadyExistsException;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointFotCommentMapper;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
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
  private final Pattern abbreviationPattern = Pattern.compile("^[A-Z0-9]{6}$");

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
    if(searchRequest == null || searchRequest.getValue() == null || searchRequest.getValue().length() <2 ){
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
    addGeoReferenceInformation(servicePointVersion);
    return ServicePointVersionMapper.toModel(servicePointService.save(servicePointVersion));
  }

  @Override
  public List<ReadServicePointVersionModel> updateServicePoint(Long id,
      CreateServicePointVersionModel createServicePointVersionModel) {

    ServicePointVersion servicePointVersionToUpdate = servicePointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    ServicePointVersion editedVersion = ServicePointVersionMapper.toEntity(createServicePointVersionModel);

    // ----- Mein Teil ------
    String abbreviation = createServicePointVersionModel.getAbbreviation();
    boolean isBussinesOrganisationInList = false;

    //TODO: Liste mit definierten Geschäftsorganisationen. -> Noch auslagern.
    //TODO: Beispieldaten mit echten Daten ersetzen -> Liste erhalten
    List<String> givenBussinesOrganisationsAbbreviationList = new ArrayList<>();
    givenBussinesOrganisationsAbbreviationList.add("ch:1:sboid:100649");

    //TODO: Check if Bo of new Service Point exist in given List
    for (String element : givenBussinesOrganisationsAbbreviationList){
      if (element.contains(editedVersion.getBusinessOrganisation())){
        //TODO: Naming anpassen
        isBussinesOrganisationInList = true;
      }
    }

    //TODO Check if BO from new SP is in List -> True
    //TODO Check if current Version is Null -> True
    //TODO Check if current Version has Abbreviation -> False
    if (
        isBussinesOrganisationInList &&
        servicePointVersionToUpdate.getAbbreviation() == null ||
            //TODO: Check brauchts diese Prüfung überhaupt
        servicePointVersionToUpdate.getAbbreviation().equals(createServicePointVersionModel.getAbbreviation())) {

      //TODO: Check Abbreviation if is Valid:
      //TODO: Check is Abbreviation greater than 6 -> not valid
      //TODO Check if Abbreviation is null or empty
      //!abbreviation.equals(abbreviation.toUpperCase() -> can check if all is uppercase
      if(createServicePointVersionModel.getAbbreviation().length() <= 6 && isAbbreviationValid(abbreviation)){
        //TODO: Check has Abbreviation small letters or other chars

        //TODO: Set abbreviation
        editedVersion.setAbbreviation(createServicePointVersionModel.getAbbreviation());

        //TODO: Prüfen ob Abkürzung eindeutig ist.
      };

    }

    //TODO: Prüfen ob ältere Version eine Abkürzung hat und ob sich diese mit der neuen unterscheidet.

    //TODO: if abbreviation vorhanden -> then not possible to update, change or delete it,


    // TODO If Abbrevation Validation Fails, then add exception -  not allowed to change or to delete abbreviation

    addGeoReferenceInformation(editedVersion);

    servicePointService.update(servicePointVersionToUpdate, editedVersion,
        servicePointService.findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber()));

    return servicePointService.findAllByNumberOrderByValidFrom(servicePointVersionToUpdate.getNumber())
        .stream()
        .map(ServicePointVersionMapper::toModel)
        .toList();
  }


  //TODO: In Service Verschieben
  public boolean isAbbreviationValid(String textToCheck) {
    return abbreviationPattern.matcher(textToCheck).matches();
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

}
