package ch.sbb.atlas.servicepointdirectory.service.georeference;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class GeoReferenceServiceTest {

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
  }
}