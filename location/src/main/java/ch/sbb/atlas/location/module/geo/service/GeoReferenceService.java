package ch.sbb.atlas.location.module.geo.service;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminChClient;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminParams;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminResponse;
import ch.sbb.atlas.location.module.geo.client.geoadmin.Layers;
import ch.sbb.atlas.location.module.geo.client.journepoy.JourneyPoiClientBase;
import ch.sbb.atlas.location.module.geo.exception.HeightNotCalculatableException;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import feign.FeignException.FeignClientException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceService {

  private final GeoAdminChClient geoAdminChClient;
  private final JourneyPoiClientBase journeyPoiClient;

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  public GeoReference getGeoReferenceWithHeight(CoordinatePair coordinatePair) {
    GeoReference geoReference = getGeoReference(coordinatePair);
    GeoAdminHeightResponse geoAdminHeightResponse = getHeight(coordinatePair);
    geoReference.setHeight(geoAdminHeightResponse.getHeight());
    return geoReference;
  }

  public GeoReference getGeoReference(CoordinatePair coordinatePair) {
    GeoAdminResponse geoAdminResponse = geoAdminChClient.getGeoReference(new GeoAdminParams(coordinatePair));
    GeoReference geoReference = toGeoReference(geoAdminResponse);

    if (geoReference.getCountry() == null) {
      return getRokasOsmInformation(coordinatePair);
    }
    return geoReference;
  }

  public GeoAdminHeightResponse getHeight(CoordinatePair coordinatePair) {
    if (coordinatePair.getSpatialReference() != SpatialReference.LV95) {
      coordinatePair = coordinateTransformer.transform(coordinatePair, SpatialReference.LV95);
    }
    try {
      return geoAdminChClient.getHeight(coordinatePair.getEast(), coordinatePair.getNorth());
    } catch (FeignClientException e) {
      return handleFeignClientException(e);
    } catch (Exception e) {
      log.error("GeoAdmin height request failed for coordinates: {} (east), {} (north)", coordinatePair.getEast(),
          coordinatePair.getNorth(), e);
      throw new HeightNotCalculatableException();
    }
  }

  private static GeoReference toGeoReference(GeoAdminResponse geoAdminResponse) {
    GeoReference result = new GeoReference();

    geoAdminResponse.getLatestResultByLayer(Layers.MUNICIPALITY).ifPresent(i -> {
      result.setSwissMunicipalityName(i.getAttributes().getMunicipalityName());
      result.setSwissMunicipalityNumber(i.getAttributes().getMunicipalityNumber());
    });
    geoAdminResponse.getLatestResultByLayer(Layers.DISTRICT).ifPresent(i -> {
      result.setSwissDistrictName(i.getAttributes().getName());
      result.setSwissDistrictNumber(Integer.parseInt(i.getFeatureId()));
    });
    geoAdminResponse.getLatestResultByLayer(Layers.LOCALITY)
        .ifPresent(i -> result.setSwissLocalityName(i.getAttributes().getLongText()));
    geoAdminResponse.getLatestResultByLayer(Layers.CANTON)
        .ifPresent(i -> result.setSwissCanton(SwissCanton.fromCantonName(i.getAttributes().getName())));
    geoAdminResponse.getLatestResultByLayer(Layers.COUNTRY).ifPresent(i -> result.setCountry(Country.fromIsoCode(i.getId())));

    return result;
  }

  private GeoReference getRokasOsmInformation(CoordinatePair coordinatePair) {
    CoordinatePair coordinatesInWgs84 = coordinatePair;
    if (coordinatePair.getSpatialReference() != SpatialReference.WGS84) {
      coordinatesInWgs84 = coordinateTransformer.transform(coordinatePair, SpatialReference.WGS84);
    }

    GeoReference result = new GeoReference();
    ch.sbb.atlas.journey.poi.model.Country body = journeyPoiClient.closestCountry(
        BigDecimal.valueOf(coordinatesInWgs84.getEast()),
        BigDecimal.valueOf(coordinatesInWgs84.getNorth())).getBody();

    String isoCountryCode = Optional.ofNullable(body)
        .map(ch.sbb.atlas.journey.poi.model.Country::getCountryCode)
        .map(ch.sbb.atlas.journey.poi.model.CountryCode::getIsoCountryCode)
        .orElse(null);
    result.setCountry(Country.fromIsoCode(isoCountryCode));
    return result;
  }

  private GeoAdminHeightResponse handleFeignClientException(FeignClientException e) {
    if (e.status() == HttpStatus.BAD_REQUEST.value()) {
      return new GeoAdminHeightResponse();
    } else {
      throw new HeightNotCalculatableException();
    }
  }
}