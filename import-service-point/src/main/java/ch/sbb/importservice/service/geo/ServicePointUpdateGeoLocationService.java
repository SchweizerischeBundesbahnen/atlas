package ch.sbb.importservice.service.geo;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.client.ServicePointClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@StepScope
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePointUpdateGeoLocationService {

  public static final String GEO_LOCATION_VERSIONS_KEY = "GeoLocationVersions";

  @Value("#{stepExecution}")
  private StepExecution stepExecution;

  private final ServicePointClient servicePointClient;

  public List<ServicePointSwissWithGeoLocationModel> getActualServicePointWithGeolocation() {
    log.info("Get service points with geo location to update...");
    List<ServicePointSwissWithGeoLocationModel> servicePointWithGeolocation =
        servicePointClient.getActualSwissServicePointWithGeolocation();
    int servicePointVersionWithGeolocationSize = servicePointWithGeolocation.stream()
        .mapToInt(swissWithGeoModel -> swissWithGeoModel.getDetails().size()).sum();
    log.info("Found {} service points with geo location to update.", servicePointVersionWithGeolocationSize);
    stepExecution.getExecutionContext().put(GEO_LOCATION_VERSIONS_KEY, servicePointVersionWithGeolocationSize);
    return servicePointWithGeolocation;
  }

  public GeoUpdateItemResultModel updateServicePointGeoLocation(String sloid, Long id) {
    return servicePointClient.updateServicePointGeoLocation(sloid, id);
  }
}
