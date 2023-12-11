package ch.sbb.atlas.servicepointdirectory.service.georeference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import java.math.BigDecimal;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

@IntegrationTest
class GeoReferenceServiceTest {

  @MockBean
  private JourneyPoiConfig journeyPoiConfig;
  @MockBean
  private JourneyPoiClient journeyPoiClient;

  @Autowired
  private GeoReferenceService geoReferenceService;

  @Test
  void shouldGetInformationAboutLocationInSwitzerland() {
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

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
  void shouldGetInformationAboutLocationAbroadViaPoiClientTransformingToWgs84() {
    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);

    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(4047745.97821)
        .north(1411920.22041)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.ROMANIA)
        .build();

    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verify(journeyPoiClient).closestCountry(BigDecimal.valueOf(26.75401227989), BigDecimal.valueOf(47.25201833567));
  }

  @Test
  void shouldGetInformationAboutLocationAbroadViaPoiClientByUsingWgs84() {
    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);

    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(47.25201833567)
        .north(26.7540122798)
        .build();
    GeoReference geoReference = geoReferenceService.getGeoReference(coordinate);

    GeoReference expectedGeoReference = GeoReference.builder()
        .country(Country.ROMANIA)
        .build();

    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verify(journeyPoiClient).closestCountry(BigDecimal.valueOf(47.25201833567), BigDecimal.valueOf(26.7540122798));
  }

  @Test
  void shouldGetHeightOfValidLV95SwissCoordinates(){
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();

    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();

    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldGetHeightOfValidWGS84SwissCoordinates(){
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(7.03523000710)
        .north(46.42533000875)
        .build();

    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();

    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldGetHeightOfValidWGS84WEBSwissCoordinates(){
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84WEB)
        .east(783158.2220039304)
        .north(5848772.61114715)
        .build();

    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(1201D)
        .build();

    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }

  @Test
  void shouldNotGetForeignCoordinates(){
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.WGS84)
        .east(10.32713502296)
        .north(55.56215489276)
        .build();

    GeoAdminHeightResponse geoAdminHeightResponse = geoReferenceService.getHeight(coordinate);

    GeoAdminHeightResponse expectedHeightResponse = GeoAdminHeightResponse.builder()
        .height(null)
        .build();

    assertThat(geoAdminHeightResponse).isEqualTo(expectedHeightResponse);
  }
}