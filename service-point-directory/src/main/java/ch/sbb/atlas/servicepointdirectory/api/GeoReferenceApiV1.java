package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoAdminHeightResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GeoData")
@RequestMapping("v1/geodata")
public interface GeoReferenceApiV1 {

  @GetMapping("/reverse-geocode")
  GeoReference getLocationInformation(CoordinatePair coordinatePair);



  //TODO: should support WGS84, WGS84Web und LV95 - done
  //TODO: only switzerland allowed to calculate height
  //TODO: Connect API from admin.ch - done
  @GetMapping("/height")
  GeoAdminHeightResponse getHeight(@RequestBody CoordinatePair coordinatePair);

}
