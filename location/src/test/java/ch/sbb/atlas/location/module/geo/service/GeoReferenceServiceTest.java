package ch.sbb.atlas.location.module.geo.service;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminChClient;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminParams;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminResponse;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminResponse.Attributes;
import ch.sbb.atlas.location.module.geo.client.geoadmin.GeoAdminResponse.Result;
import ch.sbb.atlas.location.module.geo.client.geoadmin.Layers;
import ch.sbb.atlas.location.module.geo.client.journepoy.JourneyPoiClientBase;
import ch.sbb.atlas.location.module.geo.client.journepoy.JourneyPoiConfig;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import feign.FeignException.FeignClientException;
import feign.Request;
import feign.Request.HttpMethod;
import java.math.BigDecimal;
import java.time.Year;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class GeoReferenceServiceTest {

  @MockitoBean
  private GeoAdminChClient geoAdminChClient;
  @MockitoBean
  private JourneyPoiConfig journeyPoiConfig;
  @MockitoBean
  private JourneyPoiClientBase journeyPoiClient;

  @Autowired
  private GeoReferenceService geoReferenceService;

  @Test
  void shouldGetInformationAboutLocationInSwitzerland() {
    // given
    when(geoAdminChClient.getGeoReference(any(GeoAdminParams.class))).thenReturn(createSwissGeoAdminResponse());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

    // then
    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.VAUD)
        .swissDistrictNumber(2230)
        .swissDistrictName("Riviera-Pays-d'Enhaut")
        .swissMunicipalityNumber(5841)
        .swissMunicipalityName("Château-d'Oex")
        .swissLocalityName("La Lécherette")
        .build();
    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verifyNoInteractions(journeyPoiClient);
  }

  @Test
  void shouldHandleGeoReferenceErrorsGracefully() {
    // given
    FeignClientException feignClientException = new FeignClientException(200, "",
        Request.create(HttpMethod.GET, "", Collections.emptyMap(), null, null, null), null, null);
    when(geoAdminChClient.getGeoReference(any(GeoAdminParams.class))).thenThrow(feignClientException);

    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();

    // when
    assertThatExceptionOfType(AtlasException.class)
        .isThrownBy(() -> geoReferenceService.getGeoReference(coordinate))
        .withMessage("GeoReference Service not available");
  }

  @Test
  void shouldGetInformationAboutLocationAbroadViaPoiClientTransformingToWgs84() {
    // given
    when(geoAdminChClient.getGeoReference(any(GeoAdminParams.class))).thenReturn(createEmptyGeoAdminResponse());

    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(4047745.97821)
        .north(1411920.22041)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

    // then
    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.ROMANIA)
        .build();
    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verify(journeyPoiClient).closestCountry(BigDecimal.valueOf(26.75401227989), BigDecimal.valueOf(47.25201833567));
  }

  @Test
  void shouldGetInformationAboutLocationAbroadViaPoiClientByUsingWgs84() {
    // given
    when(geoAdminChClient.getGeoReference(any(GeoAdminParams.class))).thenReturn(createEmptyGeoAdminResponse());

    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(47.25201833567)
        .north(26.7540122798)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

    // then
    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.ROMANIA)
        .build();
    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verify(journeyPoiClient).closestCountry(BigDecimal.valueOf(47.25201833567), BigDecimal.valueOf(26.7540122798));
  }

  @Test
  void shouldGetGeoReferenceWithHeight() {
    // given
    when(geoAdminChClient.getGeoReference(any(GeoAdminParams.class))).thenReturn(createSwissGeoAdminResponse());
    when(geoAdminChClient.getHeight(anyDouble(), anyDouble()))
        .thenReturn(GeoAdminHeightResponse.builder().height(1201D).build());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReferenceWithHeight(coordinate);

    // then
    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.VAUD)
        .swissDistrictNumber(2230)
        .swissDistrictName("Riviera-Pays-d'Enhaut")
        .swissMunicipalityNumber(5841)
        .swissMunicipalityName("Château-d'Oex")
        .swissLocalityName("La Lécherette")
        .height(1201.0)
        .build();
    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verifyNoInteractions(journeyPoiClient);
  }

  @Test
  void shouldGetHeightOfValidLV95SwissCoordinates() {
    // given
    when(geoAdminChClient.getHeight(anyDouble(), anyDouble()))
        .thenReturn(GeoAdminHeightResponse.builder().height(1201D).build());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    // then
    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();
    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldGetHeightOfValidWGS84SwissCoordinates() {
    // given
    when(geoAdminChClient.getHeight(anyDouble(), anyDouble()))
        .thenReturn(GeoAdminHeightResponse.builder().height(1201D).build());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(7.03523000710)
        .north(46.42533000875)
        .build();
    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    // then
    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();
    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldGetHeightOfValidWGS84WEBSwissCoordinates() {
    // given
    when(geoAdminChClient.getHeight(anyDouble(), anyDouble()))
        .thenReturn(GeoAdminHeightResponse.builder().height(1201D).build());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84WEB)
        .east(783158.2220039304)
        .north(5848772.61114715)
        .build();
    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    // then
    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();
    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldNotGetForeignCoordinates() {
    // given
    when(geoAdminChClient.getHeight(anyDouble(), anyDouble()))
        .thenReturn(GeoAdminHeightResponse.builder().height(null).build());

    // when
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(10.32713502296)
        .north(55.56215489276)
        .build();
    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    // then
    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(null)
        .build();
    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  private static GeoAdminResponse createSwissGeoAdminResponse() {
    GeoAdminResponse response = new GeoAdminResponse();

    Result municipalityResult = new Result();
    municipalityResult.setLayerBodId(Layers.MUNICIPALITY.getLayerBodId());
    Attributes municipalityAttrs = new Attributes();
    municipalityAttrs.setMunicipalityName("Château-d'Oex");
    municipalityAttrs.setMunicipalityNumber(5841);
    municipalityAttrs.setYear(Year.now(ZoneId.of(ZURICH_ZONE_ID)).getValue());
    municipalityResult.setAttributes(municipalityAttrs);

    Result districtResult = new Result();
    districtResult.setLayerBodId(Layers.DISTRICT.getLayerBodId());
    districtResult.setFeatureId("2230");
    Attributes districtAttrs = new Attributes();
    districtAttrs.setName("Riviera-Pays-d'Enhaut");
    districtResult.setAttributes(districtAttrs);

    Result localityResult = new Result();
    localityResult.setLayerBodId(Layers.LOCALITY.getLayerBodId());
    Attributes localityAttrs = new Attributes();
    localityAttrs.setLongText("La Lécherette");
    localityResult.setAttributes(localityAttrs);

    Result cantonResult = new Result();
    cantonResult.setLayerBodId(Layers.CANTON.getLayerBodId());
    Attributes cantonAttrs = new Attributes();
    cantonAttrs.setName("Vaud");
    cantonResult.setAttributes(cantonAttrs);

    Result countryResult = new Result();
    countryResult.setLayerBodId(Layers.COUNTRY.getLayerBodId());
    countryResult.setId("CH");
    countryResult.setAttributes(new Attributes());

    response.setResults(List.of(municipalityResult, districtResult, localityResult, cantonResult, countryResult));
    return response;
  }

  private static GeoAdminResponse createEmptyGeoAdminResponse() {
    GeoAdminResponse response = new GeoAdminResponse();
    response.setResults(Collections.emptyList());
    return response;
  }
}
