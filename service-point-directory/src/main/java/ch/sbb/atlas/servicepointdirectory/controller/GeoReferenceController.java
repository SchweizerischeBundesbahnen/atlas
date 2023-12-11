package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.api.GeoReferenceApiV1;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceController implements GeoReferenceApiV1 {

  private final GeoReferenceService geoReferenceService;

  @Override
  public GeoReference getLocationInformation(CoordinatePair coordinatePair) {
    return geoReferenceService.getGeoReference(coordinatePair);
  }

}
