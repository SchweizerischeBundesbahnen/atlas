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
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

@IntegrationTest
class GeoReferenceServiceTest {

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
        .build();

    assertThat(geoReference).isEqualTo(expectedGeoReference);
    verifyNoInteractions(journeyPoiClient);
  }

  @Test
  void shouldGetInformationAboutLocationAbroadViaPoiClient() {
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
}