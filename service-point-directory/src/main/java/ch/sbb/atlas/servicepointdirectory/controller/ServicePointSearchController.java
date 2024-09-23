package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.servicepointdirectory.api.ServicePointSearchApiV1;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointSearchController implements ServicePointSearchApiV1 {

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
