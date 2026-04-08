package ch.sbb.atlas.location.module.geo.api;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// todo: automatic tests + manual tests + send request directly from frontend
// todo: check if e2e tests need change
@RequestMapping("/internal/geo-reference")
public interface GeoReferenceApiInternal {

  @GetMapping
  GeoReference getLocationInformation(@Valid CoordinatePair coordinatePair,
      @RequestParam(value = "includeHeight", defaultValue = "false") boolean includeHeight);

  @GetMapping("height")
  GeoAdminHeightResponse getHeight(@Valid CoordinatePair coordinatePair);
}
