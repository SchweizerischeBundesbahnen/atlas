package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoReferenceService {

  private final GeoAdminChClient geoAdminChClient;

  public GeoReference getGeoReference(CoordinatePair coordinatePair) {
    GeoAdminResponse geoAdminResponse = geoAdminChClient.getGeoReference(new GeoAdminParams(coordinatePair));
    return toGeoReference(geoAdminResponse);
  }

  private static GeoReference toGeoReference(GeoAdminResponse geoAdminResponse) {
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

    return result;
  }

}
