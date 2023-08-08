package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.LoadingPointVersionModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointItemImportResult;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.LoadingPointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.LoadingPointVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointImportService;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointElementRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoadingPointController implements LoadingPointApiV1 {

  private final LoadingPointService loadingPointService;
  private final LoadingPointImportService loadingPointImportService;

  @Override
  public Container<LoadingPointVersionModel> getLoadingPoints(Pageable pageable, LoadingPointElementRequestParams loadingPointElementRequestParams) {
    LoadingPointSearchRestrictions loadingPointSearchRestrictions = LoadingPointSearchRestrictions.builder()
            .pageable(pageable)
            .loadingPointElementRequestParams(loadingPointElementRequestParams)
            .build();

    Page<LoadingPointVersion> loadingPointVersions = loadingPointService.findAll(loadingPointSearchRestrictions);
    return Container.<LoadingPointVersionModel>builder()
        .objects(loadingPointVersions.stream().map(LoadingPointVersionMapper::fromEntity).toList())
        .totalCount(loadingPointVersions.getTotalElements())
        .build();
  }


  @Override
  public List<LoadingPointVersionModel> getLoadingPoint(Integer servicePointNumber, Integer loadingPointNumber) {
    ServicePointNumber number = ServicePointNumber.of(servicePointNumber);
    List<LoadingPointVersionModel> loadingPointVersions = loadingPointService.findLoadingPoint(number,
            loadingPointNumber)
        .stream()
        .map(LoadingPointVersionMapper::fromEntity).toList();
    if (loadingPointVersions.isEmpty()) {
      throw new LoadingPointNumberNotFoundException(number, loadingPointNumber);
    }
    return loadingPointVersions;
  }

  @Override
  public LoadingPointVersionModel getLoadingPointVersion(Long id) {
    return loadingPointService.findById(id).map(LoadingPointVersionMapper::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<LoadingPointItemImportResult> importLoadingPoints(LoadingPointImportRequestModel loadingPointImportRequestModel) {
    return loadingPointImportService.importLoadingPoints(loadingPointImportRequestModel.getLoadingPointCsvModelContainers());
  }

}
