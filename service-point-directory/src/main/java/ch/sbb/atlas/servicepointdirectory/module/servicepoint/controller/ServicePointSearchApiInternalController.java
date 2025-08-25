package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointSearchApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.search.ServicePointSearchResult;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointSearchApiInternalController implements ServicePointSearchApiInternal {

  private final ServicePointSearchService servicePointSearchService;

  @Override
  public List<ServicePointSearchResult> searchServicePoints(ServicePointSearchRequest searchRequest) {
    return servicePointSearchService.searchServicePointVersion(searchRequest.getValue());
  }

  @Override
  public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(ServicePointSearchRequest searchRequest) {
    return servicePointSearchService.searchServicePointsWithRouteNetworkTrue(searchRequest.getValue());
  }

  @Override
  public List<ServicePointSearchResult> searchSwissOnlyServicePoints(ServicePointSearchRequest searchRequest) {
    return servicePointSearchService.searchSwissOnlyServicePointVersion(searchRequest.getValue());
  }

}
