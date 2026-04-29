package ch.sbb.importservice.module.geo.service;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.module.geo.client.ServicePointClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePointUpdateGeoLocationService {

  private final ServicePointClient servicePointClient;

  public List<ServicePointSwissWithGeoLocationModel> getActualServicePointWithGeolocation() {
    log.info("Get service points with geo location to update...");
    List<ServicePointSwissWithGeoLocationModel> servicePointWithGeolocation =
        servicePointClient.getActualSwissServicePointWithGeolocation();
    int servicePointVersionWithGeolocationSize = servicePointWithGeolocation.stream()
        .mapToInt(swissWithGeoModel -> swissWithGeoModel.getDetails().size()).sum();
    log.info("Found {} service points with geo location to update.", servicePointVersionWithGeolocationSize);
    return servicePointWithGeolocation;
  }

  public GeoUpdateItemResultModel updateServicePointGeoLocation(String sloid, Long id) {
    return servicePointClient.updateServicePointGeoLocation(sloid, id);
  }
}
