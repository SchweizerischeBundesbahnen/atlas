package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateLoadingPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.LoadingPointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.LoadingPointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointElementRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointImportService;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointService;
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
public class LoadingPointController implements LoadingPointApiV1 {

  private final LoadingPointService loadingPointService;
  private final ServicePointService servicePointService;
  private final LoadingPointImportService loadingPointImportService;
  private final CrossValidationService crossValidationService;

  @Override
  public Container<ReadLoadingPointVersionModel> getLoadingPoints(Pageable pageable,
      LoadingPointElementRequestParams loadingPointElementRequestParams) {
    LoadingPointSearchRestrictions loadingPointSearchRestrictions = LoadingPointSearchRestrictions.builder()
        .pageable(pageable)
        .loadingPointElementRequestParams(loadingPointElementRequestParams)
        .build();

    Page<LoadingPointVersion> loadingPointVersions = loadingPointService.findAll(loadingPointSearchRestrictions);
    return Container.<ReadLoadingPointVersionModel>builder()
        .objects(loadingPointVersions.stream().map(LoadingPointVersionMapper::fromEntity).toList())
        .totalCount(loadingPointVersions.getTotalElements())
        .build();
  }

  @Override
  public Container<ReadLoadingPointVersionModel> getLoadingPointOverview(Integer servicePointNumber, Pageable pageable) {
    return loadingPointService.getOverview(servicePointNumber, pageable);
  }

  @Override
  public List<ReadLoadingPointVersionModel> getLoadingPoint(Integer servicePointNumber, Integer loadingPointNumber) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    List<ReadLoadingPointVersionModel> loadingPointVersions = loadingPointService.findLoadingPoint(number,
            loadingPointNumber)
        .stream()
        .map(LoadingPointVersionMapper::fromEntity).toList();
    if (loadingPointVersions.isEmpty()) {
      throw new LoadingPointNumberNotFoundException(number, loadingPointNumber);
    }
    return loadingPointVersions;
  }

  @Override
  public ReadLoadingPointVersionModel getLoadingPointVersion(Long id) {
    return loadingPointService.findById(id).map(LoadingPointVersionMapper::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<ItemImportResult> importLoadingPoints(LoadingPointImportRequestModel loadingPointImportRequestModel) {
    return loadingPointImportService.importLoadingPoints(loadingPointImportRequestModel.getLoadingPointCsvModelContainers());
  }

  @Override
  public ReadLoadingPointVersionModel createLoadingPoint(CreateLoadingPointVersionModel newVersion) {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(newVersion.getServicePointNumber());
    crossValidationService.validateServicePointNumberExists(servicePointNumber);

    LoadingPointVersion loadingPointVersion = loadingPointService.create(LoadingPointVersionMapper.toEntity(newVersion),
        servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber));
    return LoadingPointVersionMapper.fromEntity(loadingPointVersion);
  }

  @Override
  public List<ReadLoadingPointVersionModel> updateLoadingPoint(Long id, CreateLoadingPointVersionModel updatedVersion) {
    LoadingPointVersion loadingPointVersionToUpdate = loadingPointService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    LoadingPointVersion editedVersion = LoadingPointVersionMapper.toEntity(updatedVersion);
    editedVersion.setNumber(loadingPointVersionToUpdate.getNumber());
    editedVersion.setServicePointNumber(loadingPointVersionToUpdate.getServicePointNumber());

    List<ServicePointVersion> associatedServicePoint = servicePointService.findAllByNumberOrderByValidFrom(
        loadingPointVersionToUpdate.getServicePointNumber());
    loadingPointService.updateVersion(loadingPointVersionToUpdate, editedVersion, associatedServicePoint);
    return loadingPointService.findLoadingPoint(loadingPointVersionToUpdate.getServicePointNumber(),
            loadingPointVersionToUpdate.getNumber())
        .stream()
        .map(LoadingPointVersionMapper::fromEntity).toList();
  }

}
