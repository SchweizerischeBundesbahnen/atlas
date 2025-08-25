package ch.sbb.atlas.servicepointdirectory.module.loadingpoint.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.loadingpoint.api.LoadingPointApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.loadingpoint.service.LoadingPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoadingPointApiInternalController implements LoadingPointApiInternal {

  private final LoadingPointService loadingPointService;

  @Override
  public Container<ReadLoadingPointVersionModel> getLoadingPointOverview(Integer servicePointNumber, Pageable pageable) {
    return loadingPointService.getOverview(servicePointNumber, pageable);
  }

}
