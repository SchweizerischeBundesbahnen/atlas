package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.repository.ServicePointSearchVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicePointSearchService {

  private static final int SEARCH_RESULT_SIZE = 200;

  private final ServicePointSearchVersionRepository servicePointSearchVersionRepository;

  public List<ServicePointSearchResult> searchServicePointVersion(String value) {
    List<ServicePointSearchResult> servicePointSearchResults = servicePointSearchVersionRepository.searchServicePoints(value);
    return getSearchResults(servicePointSearchResults);
  }

  public List<ServicePointSearchResult> searchSwissOnlyServicePointVersion(String value) {
    List<ServicePointSearchResult> servicePointSearchResults =
        servicePointSearchVersionRepository.searchSwissOnlyStopPointServicePoints(value);
    return getSearchResults(servicePointSearchResults);
  }

  public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(String value) {
    List<ServicePointSearchResult> servicePointSearchResults =
        servicePointSearchVersionRepository.searchServicePointsWithRouteNetworkTrue(
            value);
    return getSearchResults(servicePointSearchResults);
  }

  private List<ServicePointSearchResult> getSearchResults(List<ServicePointSearchResult> servicePointSearchResults) {
    if (servicePointSearchResults.size() > SEARCH_RESULT_SIZE) {
      return servicePointSearchResults.subList(0, SEARCH_RESULT_SIZE);
    }
    return servicePointSearchResults;
  }
}
