package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.imports.ItemImportResponseStatus;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.api.GeoReferenceApiV1;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceJobService;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceController implements GeoReferenceApiV1 {

  private final GeoReferenceService geoReferenceService;
  private final GeoReferenceJobService geoReferenceJobService;

  @Override
  public GeoReference getLocationInformation(CoordinatePair coordinatePair) {
    return geoReferenceService.getGeoReference(coordinatePair);
  }

  @Override
  public GeoUpdateItemResultModel updateServicePointGeoLocation(String sloid, Long id) {
    try {
      UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(id);
      if (result != null) {
        return new GeoUpdateItemResultModel(result.getSloid(), result.getId(),
            result.getResponseMessage(), ItemImportResponseStatus.SUCCESS);
      }
    } catch (Exception e) {
      return new GeoUpdateItemResultModel(sloid, id, e.getMessage(), ItemImportResponseStatus.FAILED);
    }
    return null;
  }
}
