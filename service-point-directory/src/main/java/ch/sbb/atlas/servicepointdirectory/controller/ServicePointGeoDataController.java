package ch.sbb.atlas.servicepointdirectory.controller;

import static ch.sbb.atlas.imports.ItemProcessResponseStatus.FAILED;
import static ch.sbb.atlas.imports.ItemProcessResponseStatus.SUCCESS;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeoDataApiInternal;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import ch.sbb.atlas.servicepointdirectory.geodata.service.ServicePointGeoDataService;
import ch.sbb.atlas.servicepointdirectory.model.UpdateGeoLocationResultContainer;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceJobService;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@Hidden
public class ServicePointGeoDataController implements ServicePointGeoDataApiInternal {

  private final GeoReferenceService geoReferenceService;
  private final GeoReferenceJobService geoReferenceJobService;

  private final ServicePointGeoDataService servicePointGeoDataService;

  @Override
  public VectorTile.Tile getServicePointsGeoData(Integer z, Integer x, Integer y, Optional<LocalDate> validAtDate) {
    return servicePointGeoDataService.getGeoData(z, x, y, validAtDate.orElse(LocalDate.now()));
  }

  @Override
  public GeoReference getLocationInformation(CoordinatePair coordinatePair) {
    return geoReferenceService.getGeoReference(coordinatePair);
  }

  @Override
  public GeoUpdateItemResultModel updateServicePointGeoLocation(String sloid, Long id) {
    try {
      UpdateGeoLocationResultContainer result = geoReferenceJobService.updateGeoLocation(id);
      if (result != null) {
        return GeoUpdateItemResultModel.builder()
            .sloid(result.getSloid())
            .id(result.getId())
            .message(result.getResponseMessage())
            .status(SUCCESS)
            .build();
      }
    } catch (Exception e) {
      return GeoUpdateItemResultModel.builder()
          .sloid(sloid)
          .id(id)
          .message(e.getMessage())
          .status(FAILED)
          .build();
    }
    return null;
  }
}
