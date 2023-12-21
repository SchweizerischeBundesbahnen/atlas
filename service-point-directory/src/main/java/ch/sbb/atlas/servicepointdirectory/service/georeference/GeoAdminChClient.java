package ch.sbb.atlas.servicepointdirectory.service.georeference;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * API documentation: https://api3.geo.admin.ch/api/doc.html
 * Some API examples:
 * https://api3.geo.admin.ch/rest/services/api/MapServer/identify?sr=4326&geometryType=esriGeometryPoint&geometry=7.587771,47.086634&imageDisplay=0,0,0&mapExtent=0,0,0,0&tolerance=0&returnGeometry=false&layers=all:ch.swisstopo.swissboundaries3d-bezirk-flaeche.fill,ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo.swissboundaries3d-kanton-flaeche.fill,ch.swisstopo.swissboundaries3d-land-flaeche.fill
 * https://api3.geo.admin.ch/rest/services/api/MapServer/identify?geometryType=esriGeometryPoint&geometry=548945.5,147956&imageDisplay=0,0,0&mapExtent=0,0,0,0&tolerance=0&layers=all:ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo-vd.ortschaftenverzeichnis_plz&returnGeometry=false
 *
 */
@FeignClient(name = "geoAdminChClient", url = "https://api3.geo.admin.ch")
public interface GeoAdminChClient {

  @GetMapping(value = "/rest/services/api/MapServer/identify")
  GeoAdminResponse getGeoReference(@SpringQueryMap GeoAdminParams params);

  @GetMapping(value = "/rest/services/height")
  GeoAdminHeightResponse getHeight(@RequestParam double easting, @RequestParam double northing);

}
