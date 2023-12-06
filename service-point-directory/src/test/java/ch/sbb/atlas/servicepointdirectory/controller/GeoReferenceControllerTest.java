package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GeoReferenceControllerTest {

  @Mock
  private GeoReferenceService geoReferenceService;

  private GeoReferenceController geoReferenceController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    geoReferenceController = new GeoReferenceController(geoReferenceService);
  }

  @Test
  void shouldCallService() {
    CoordinatePair coordinate = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.30320000000)
        .north(1141633.69605000000)
        .build();
    GeoReference geoReference = GeoReference.builder().country(Country.SWITZERLAND).build();
    when(geoReferenceService.getGeoReference(coordinate, false)).thenReturn(geoReference);

    GeoReference locationInformation = geoReferenceController.getLocationInformation(coordinate);

    assertThat(locationInformation).isEqualTo(geoReference);
    verify(geoReferenceService).getGeoReference(coordinate, false);
  }
}