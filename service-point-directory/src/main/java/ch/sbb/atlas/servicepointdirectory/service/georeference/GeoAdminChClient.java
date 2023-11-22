package ch.sbb.atlas.servicepointdirectory.service.georeference;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geoAdminChClient", url = "https://api3.geo.admin.ch")
public interface GeoAdminChClient {

  @GetMapping(value = "/rest/services/api/MapServer/identify")
  GeoAdminResponse getGeoReference(@SpringQueryMap GeoAdminParams params);

  @GetMapping(value = "/rest/services/height")
  GeoAdminHeightResponse getHeight(@RequestParam double easting, @RequestParam double northing);

}
