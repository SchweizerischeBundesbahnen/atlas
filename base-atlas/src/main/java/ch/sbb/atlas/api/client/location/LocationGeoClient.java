package ch.sbb.atlas.api.client.location;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "locationGeoClient", url = "${atlas.client.gateway.url}", path = "location/internal/geo-reference",
    configuration = TokenPassingFeignClientConfig.class)
public interface LocationGeoClient {

  @GetMapping
  GeoReference getLocationInformation(@Valid @SpringQueryMap CoordinatePair coordinatePair,
      @RequestParam(value = "includeHeight", defaultValue = "false") boolean includeHeight);

  @GetMapping("height")
  GeoAdminHeightResponse getHeight(@Valid @SpringQueryMap CoordinatePair coordinatePair);
}