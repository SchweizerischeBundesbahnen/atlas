package ch.sbb.importservice.service.geo;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.importservice.client.ServicePointClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicePointUpdateGeoLocationService {

  private final ServicePointClient servicePointClient;

  public List<ServicePointSwissWithGeoModel> getActualServicePointWithGeolocation(){
    log.info("Get service points with geo location to update...");
    List<ServicePointSwissWithGeoModel> servicePointWithGeolocation = servicePointClient.getActualServicePointWithGeolocation();
    log.info("Found {} service points with geo location to update.", servicePointWithGeolocation.size());
    return servicePointWithGeolocation;
  }

  public List<ItemImportResult> postServicePoints(String servicePointImportRequestModel) {
    return null;
  }
}
