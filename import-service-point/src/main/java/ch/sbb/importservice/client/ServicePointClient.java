package ch.sbb.importservice.client;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApiV1;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "servicePointClient", url = "${atlas.client.gateway.url}", path = "service-point-directory",
    configuration = OAuthFeignConfig.class)
public interface ServicePointClient extends ServicePointBulkImportApiV1 {

  @GetMapping("v1/service-points/actual-swiss-service-point-with-geo")
  List<ServicePointSwissWithGeoModel> getActualServicePointWithGeolocation();
}
