package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.LoadingPointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.model.search.LoadingPointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointService;
import java.time.LocalDate;
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
public class LoadingPointController implements LoadingPointApiV1 {

  private final LoadingPointService loadingPointService;

  @Override
  public Container<LoadingPointVersionModel> getLoadingPoints(Pageable pageable, List<String> searchCriteria,
      Optional<LocalDate> validOn) {
    Page<LoadingPointVersion> loadingPointVersions = loadingPointService.findAll(
        LoadingPointSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .validOn(validOn)
            .build());
    return Container.<LoadingPointVersionModel>builder()
        .objects(loadingPointVersions.stream().map(LoadingPointVersionModel::fromEntity).toList())
        .totalCount(loadingPointVersions.getTotalElements())
        .build();
  }

  @Override
  public List<LoadingPointVersionModel> getLoadingPoint(Integer servicePointNumber, Integer loadingPointNumber) {
    ServicePointNumber number = ServicePointNumber.of(servicePointNumber);
    List<LoadingPointVersionModel> loadingPointVersions = loadingPointService.findLoadingPoint(number,
            loadingPointNumber)
        .stream()
        .map(LoadingPointVersionModel::fromEntity).toList();
    if (loadingPointVersions.isEmpty()) {
      throw new LoadingPointNumberNotFoundException(number, loadingPointNumber);
    }
    return loadingPointVersions;
  }

  @Override
  public LoadingPointVersionModel getLoadingPointVersion(Long id) {
    return loadingPointService.findById(id).map(LoadingPointVersionModel::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

}
