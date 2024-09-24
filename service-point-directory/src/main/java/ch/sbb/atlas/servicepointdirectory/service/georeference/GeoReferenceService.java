package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.exception.HeightNotCalculatableException;
import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceService {

  private final GeoAdminChClient geoAdminChClient;
  private final JourneyPoiClient journeyPoiClient;

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  public GeoReference getGeoReference(CoordinatePair coordinatePair) {
    return getGeoReference(coordinatePair, true);
  }

  public GeoReference getGeoReference(CoordinatePair coordinatePair, boolean callHeightService) {
    GeoAdminResponse geoAdminResponse = geoAdminChClient.getGeoReference(new GeoAdminParams(coordinatePair));
    GeoReference swissTopoInformation = toGeoReference(geoAdminResponse);

    if(callHeightService){
        GeoAdminHeightResponse geoAdminHeightResponse = getHeight(coordinatePair);
        swissTopoInformation.setHeight(geoAdminHeightResponse.getHeight());
    }

    if (swissTopoInformation.getCountry() == null) {
      return getRokasOsmInformation(coordinatePair);
    }
    return swissTopoInformation;
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
        .map(CountryCode::getIsoCountryCode)
        .orElse(null);
    result.setCountry(Country.fromIsoCode(isoCountryCode));
    return result;
  }
  public GeoAdminHeightResponse getHeight(CoordinatePair coordinatePair) {
    if(coordinatePair.getSpatialReference() != SpatialReference.LV95){
      coordinatePair = coordinateTransformer.transform(coordinatePair, SpatialReference.LV95);
    }
    try {
      return geoAdminChClient.getHeight(coordinatePair.getEast(), coordinatePair.getNorth());
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

  public void addGeoReferenceInformation(ServicePointVersion servicePointVersion) {
    if (servicePointVersion.hasGeolocation()) {
      ServicePointGeolocation servicePointGeolocation = servicePointVersion.getServicePointGeolocation();
      GeoReference geoReference = getGeoReference(servicePointGeolocation.asCoordinatePair(),servicePointGeolocation.getHeight() == null);

      if (geoReference.getHeight() != null) {
        servicePointGeolocation.setHeight(geoReference.getHeight());
      }

      servicePointGeolocation.setCountry(geoReference.getCountry());
      servicePointGeolocation.setSwissCanton(geoReference.getSwissCanton());
      servicePointGeolocation.setSwissDistrictNumber(geoReference.getSwissDistrictNumber());
      servicePointGeolocation.setSwissDistrictName(geoReference.getSwissDistrictName());
      servicePointGeolocation.setSwissMunicipalityNumber(geoReference.getSwissMunicipalityNumber());
      servicePointGeolocation.setSwissMunicipalityName(geoReference.getSwissMunicipalityName());
      servicePointGeolocation.setSwissLocalityName(geoReference.getSwissLocalityName());
    }
  }
}