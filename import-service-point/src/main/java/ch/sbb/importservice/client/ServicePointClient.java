package ch.sbb.importservice.client;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "servicePointClient", url = "${atlas.client.gateway.url}", path = "service-point-directory",
    configuration = OAuthFeignConfig.class)
public interface ServicePointClient {

  @GetMapping("v1/service-points/actual-swiss-service-point-with-geo")
  List<ServicePointSwissWithGeoLocationModel> getActualSwissServicePointWithGeolocation();

  @PutMapping("v1/geodata/update-geo/{sloid}/{id}")
  GeoUpdateItemResultModel updateServicePointGeoLocation(@PathVariable("sloid") String sloid, @PathVariable("id") Long id);
}
