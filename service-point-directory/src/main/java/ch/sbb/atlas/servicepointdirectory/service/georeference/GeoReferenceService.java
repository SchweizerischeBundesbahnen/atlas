package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.HeightNotCalculatableException;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.GeometryTransformer;
import feign.FeignException.FeignClientException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceService {

  private final GeoAdminChClient geoAdminChClient;
  private final JourneyPoiClient journeyPoiClient;

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  private final GeometryTransformer geometryTransformer;

  public GeoReference getGeoReference(CoordinatePair coordinatePair) {
    Optional<GeoAdminHeightResponse> geoAdminHeightResponse = Optional.ofNullable(getHeight(coordinatePair));

    GeoAdminResponse geoAdminResponse = geoAdminChClient.getGeoReference(new GeoAdminParams(coordinatePair));
    GeoReference swissTopoInformation = toGeoReference(geoAdminResponse, geoAdminHeightResponse);
    if (swissTopoInformation.getCountry() == null) {
      return getRokasOsmInformation(coordinatePair);
    }
    return swissTopoInformation;
  }

  public GeoReference getGeoReferenceWithoutHeight(CoordinatePair coordinatePair){
    GeoAdminResponse geoAdminResponse = geoAdminChClient.getGeoReference(new GeoAdminParams(coordinatePair));
    GeoReference swissTopoInformation = toGeoReference(geoAdminResponse, Optional.empty());
    if (swissTopoInformation.getCountry() == null) {
      return getRokasOsmInformation(coordinatePair);
    }
    return swissTopoInformation;
  }

  private static GeoReference toGeoReference(GeoAdminResponse geoAdminResponse, Optional<GeoAdminHeightResponse> geoAdminHeightResponse) {
    GeoReference result = new GeoReference();

    geoAdminResponse.getResultByLayer(Layers.MUNICIPALITY).ifPresent(i -> {
      result.setSwissMunicipalityName(i.getAttributes().getGemname());
      result.setSwissMunicipalityNumber(Integer.parseInt(i.getFeatureId()));
    });
    geoAdminResponse.getResultByLayer(Layers.DISTRICT).ifPresent(i -> {
      result.setSwissDistrictName(i.getAttributes().getName());
      result.setSwissDistrictNumber(Integer.parseInt(i.getFeatureId()));
    });
    geoAdminResponse.getResultByLayer(Layers.LOCALITY)
        .ifPresent(i -> result.setSwissLocalityName(i.getAttributes().getLangtext()));
    geoAdminResponse.getResultByLayer(Layers.CANTON)
        .ifPresent(i -> result.setSwissCanton(SwissCanton.fromCantonNumber(Integer.parseInt(i.getFeatureId()))));
    geoAdminResponse.getResultByLayer(Layers.COUNTRY).ifPresent(i -> result.setCountry(Country.fromIsoCode(i.getId())));

    geoAdminHeightResponse.ifPresent(heightResponse -> result.setHeight(heightResponse.height));

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
        .map(CountryCode::getIsoCountryCode)
        .orElse(null);
    result.setCountry(Country.fromIsoCode(isoCountryCode));
    return result;
  }
  public GeoAdminHeightResponse getHeight(CoordinatePair coordinatePair) {
    Coordinate coordinate = new Coordinate(coordinatePair.getEast(), coordinatePair.getNorth());
    if(coordinatePair.getSpatialReference() != SpatialReference.LV95){
      coordinate = geometryTransformer.transform(coordinatePair.getSpatialReference(), coordinate, SpatialReference.LV95);
    }

    try {
      return geoAdminChClient.getHeight(coordinate.getX(), coordinate.getY());
    }
    catch (FeignClientException e){
      return handleFeignClientException(e);
    } catch (Exception e) {
      throw new HeightNotCalculatableException();
    }
  }

  private GeoAdminHeightResponse handleFeignClientException(FeignClientException e) {
    if (e.status() == HttpStatus.BAD_REQUEST.value()) {
      return new GeoAdminHeightResponse();
    } else {
      throw new HeightNotCalculatableException();
    }
  }

  public void getHeightForServicePoint(ServicePointVersion servicePointVersion) {
    ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
    if (servicePointGeolocation != null && servicePointGeolocation.getHeight() == null) {
      GeoAdminHeightResponse geoAdminHeightResponse = getHeight(servicePointGeolocation.asCoordinatePair());
      servicePointGeolocation.setHeight(geoAdminHeightResponse.getHeight());
    }
  }

  public void getHeightForTrafficPoint(TrafficPointElementVersion trafficPointElementVersion) {
    TrafficPointElementGeolocation trafficPointElementGeolocation = trafficPointElementVersion.getTrafficPointElementGeolocation();
    if (trafficPointElementGeolocation != null && trafficPointElementGeolocation.getHeight() == null) {
      GeoAdminHeightResponse geoAdminHeightResponse = getHeight(trafficPointElementGeolocation.asCoordinatePair());
      trafficPointElementGeolocation.setHeight(geoAdminHeightResponse.getHeight());
    }
  }
}