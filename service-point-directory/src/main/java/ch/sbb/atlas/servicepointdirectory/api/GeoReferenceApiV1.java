package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "GeoData")
@RequestMapping("v1/geodata")
public interface GeoReferenceApiV1 {

  @GetMapping("/reverse-geocode")
  GeoReference getLocationInformation(CoordinatePair coordinatePair);

}
