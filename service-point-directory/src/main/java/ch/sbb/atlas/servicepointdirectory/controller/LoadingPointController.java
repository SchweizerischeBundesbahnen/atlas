package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointApiV1;
import ch.sbb.atlas.servicepointdirectory.api.LoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.LoadingPointNumberNotFoundException;
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
  // TODO: add filter parameter
  public Container<LoadingPointVersionModel> getLoadingPoints(Pageable pageable, Optional<LocalDate> validOn) {
    Page<LoadingPointVersion> LoadingPointVersions = loadingPointService.findAll(pageable);
    return Container.<LoadingPointVersionModel>builder()
        .objects(LoadingPointVersions.stream().map(LoadingPointVersionModel::fromEntity).toList())
        .totalCount(LoadingPointVersions.getTotalElements())
        .build();
  }

  @Override
  // What identifies Ladestelle? Nummer ?
  public List<LoadingPointVersionModel> getLoadingPoint(Integer loadingPointNumber) {
    List<LoadingPointVersionModel> LoadingPointVersions = loadingPointService.findLoadingPointVersions(loadingPointNumber)
        .stream()
        .map(LoadingPointVersionModel::fromEntity).toList();
    if (LoadingPointVersions.isEmpty()) {
      throw new LoadingPointNumberNotFoundException(loadingPointNumber);
    }
    return LoadingPointVersions;
  }

  @Override
  public LoadingPointVersionModel getLoadingPointVersion(Long id) {
    return loadingPointService.findById(id).map(LoadingPointVersionModel::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

}
