package ch.sbb.atlas.location.module.geo.controller;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.location.module.geo.api.GeoReferenceApiInternal;
import ch.sbb.atlas.location.module.geo.service.GeoReferenceService;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceApiInternalController implements GeoReferenceApiInternal {

  private final GeoReferenceService geoReferenceService;

  @Override
  public GeoReference getLocationInformation(CoordinatePair coordinatePair, boolean includeHeight) {
    if (includeHeight) {
      return geoReferenceService.getGeoReferenceWithHeight(coordinatePair);
    }
    return geoReferenceService.getGeoReference(coordinatePair);
  }

  @Override
  public GeoAdminHeightResponse getHeight(CoordinatePair coordinatePair) {
    return geoReferenceService.getHeight(coordinatePair);
  }

}