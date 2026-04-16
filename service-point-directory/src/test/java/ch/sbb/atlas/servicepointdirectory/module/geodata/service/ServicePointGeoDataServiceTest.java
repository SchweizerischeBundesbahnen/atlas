package ch.sbb.atlas.servicepointdirectory.module.geodata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.location.GeoAdminHeightResponse;
import ch.sbb.atlas.api.client.location.LocationGeoClient;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.geodata.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.module.geodata.mapper.ServicePointGeoDataMapper;
import ch.sbb.atlas.servicepointdirectory.module.geodata.repository.ServicePointGeolocationRepository;
import ch.sbb.atlas.servicepointdirectory.module.geodata.transformer.BoundingBoxTransformer;
import ch.sbb.atlas.servicepointdirectory.module.geodata.transformer.GeometryTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicePointGeoDataServiceTest {

  @Mock
  private BoundingBoxTransformer boundingBoxTransformer;
  @Mock
  private GeometryTransformer geometryTransformer;
  @Mock
  private VectorTileService vectorTileService;
  @Mock
  private ServicePointGeoDataMapper servicePointGeoDataMapper;
  @Mock
  private ServicePointGeolocationRepository geolocationRepository;
  @Mock
  private LocationGeoClient locationGeoClient;

  private ServicePointGeoDataService servicePointGeoDataService;

  @BeforeEach
  void setUp() {
    servicePointGeoDataService = new ServicePointGeoDataService(
        boundingBoxTransformer, geometryTransformer, vectorTileService,
        servicePointGeoDataMapper, geolocationRepository, locationGeoClient);
  }

  @Test
  void shouldGetGeoReferenceInformationWithoutHeight() {
    // given
    ServicePointGeolocation geolocation = ServicePointGeolocation.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2674198D)
        .north(1244494D)
        .height(100.2)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .build();

    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictNumber(246)
        .swissDistrictName("Bern-Mittelland")
        .swissMunicipalityNumber(351)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .build();

    when(locationGeoClient.getLocationInformation(any(CoordinatePair.class), eq(false)))
        .thenReturn(geoReference);

    // when
    ServicePointGeolocation result = servicePointGeoDataService.getGeoReferenceInformation(geolocation);

    // then
    assertThat(result.getCountry()).isEqualTo(Country.SWITZERLAND);
    assertThat(result.getSwissCanton()).isEqualTo(SwissCanton.BERN);
    assertThat(result.getSwissDistrictNumber()).isEqualTo(246);
    assertThat(result.getSwissDistrictName()).isEqualTo("Bern-Mittelland");
    assertThat(result.getSwissMunicipalityNumber()).isEqualTo(351);
    assertThat(result.getSwissMunicipalityName()).isEqualTo("Bern");
    assertThat(result.getSwissLocalityName()).isEqualTo("Bern");
    assertThat(result.getHeight()).isEqualTo(100.2);
    verify(locationGeoClient).getLocationInformation(any(CoordinatePair.class), eq(false));
  }

  @Test
  void shouldGetGeoReferenceInformationWithHeight() {
    // given
    ServicePointGeolocation geolocation = ServicePointGeolocation.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2674198D)
        .north(1244494D)
        .height(null)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .build();

    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.VAUD)
        .swissDistrictNumber(2230)
        .swissDistrictName("Riviera-Pays-d'Enhaut")
        .swissMunicipalityNumber(5841)
        .swissMunicipalityName("Château-d'Oex")
        .swissLocalityName("La Lécherette")
        .height(1201.0)
        .build();

    when(locationGeoClient.getLocationInformation(any(CoordinatePair.class), eq(true)))
        .thenReturn(geoReference);

    // when
    ServicePointGeolocation result = servicePointGeoDataService.getGeoReferenceInformation(geolocation);

    // then
    assertThat(result.getCountry()).isEqualTo(Country.SWITZERLAND);
    assertThat(result.getSwissCanton()).isEqualTo(SwissCanton.VAUD);
    assertThat(result.getSwissDistrictNumber()).isEqualTo(2230);
    assertThat(result.getSwissDistrictName()).isEqualTo("Riviera-Pays-d'Enhaut");
    assertThat(result.getSwissMunicipalityNumber()).isEqualTo(5841);
    assertThat(result.getSwissMunicipalityName()).isEqualTo("Château-d'Oex");
    assertThat(result.getSwissLocalityName()).isEqualTo("La Lécherette");
    assertThat(result.getHeight()).isEqualTo(1201.0);
    verify(locationGeoClient).getLocationInformation(any(CoordinatePair.class), eq(true));
  }

  @Test
  void shouldKeepExistingHeightWhenGeoReferenceReturnsNull() {
    // given
    ServicePointGeolocation geolocation = ServicePointGeolocation.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2674198D)
        .north(1244494D)
        .height(555.0)
        .build();

    GeoReference geoReference = GeoReference.builder()
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .height(null)
        .build();

    when(locationGeoClient.getLocationInformation(any(CoordinatePair.class), eq(false)))
        .thenReturn(geoReference);

    // when
    ServicePointGeolocation result = servicePointGeoDataService.getGeoReferenceInformation(geolocation);

    // then
    assertThat(result.getHeight()).isEqualTo(555.0);
  }

  @Test
  void shouldGetHeight() {
    // given
    CoordinatePair coordinatePair = CoordinatePair.builder()
        .spatialReference(SpatialReference.LV95)
        .east(2568989.3032)
        .north(1141633.69605)
        .build();

    GeoAdminHeightResponse expectedResponse = GeoAdminHeightResponse.builder()
        .height(1201.0)
        .build();

    when(locationGeoClient.getHeight(coordinatePair)).thenReturn(expectedResponse);

    // when
    GeoAdminHeightResponse result = servicePointGeoDataService.getHeight(coordinatePair);

    // then
    assertThat(result).isEqualTo(expectedResponse);
    assertThat(result.getHeight()).isEqualTo(1201.0);
    verify(locationGeoClient).getHeight(coordinatePair);
  }
}