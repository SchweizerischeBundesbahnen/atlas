package ch.sbb.atlas.servicepointdirectory.repository;

import static ch.sbb.atlas.servicepointdirectory.model.TestData.testGeolocationWgs84;
import static ch.sbb.atlas.servicepointdirectory.model.TestData.testServicePoint;
import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository.coordinatesBetween;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ServicePointGeolocationTest {

  public static final Envelope ENVELOPE_WITH_DATA = new Envelope(0D, 0.1D, 0D, 0.1D);
  public static final Envelope ENVELOPE_WITHOUT_DATA = new Envelope(5D, 10D, 5D, 10D);
  private final ServicePointGeolocationRepository repository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  public ServicePointGeolocationTest(
      ServicePointGeolocationRepository repository,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.repository = repository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createTestData() {
    final ServicePointVersion servicePointVersion = testServicePoint();
    servicePointVersion.setServicePointGeolocation(testGeolocationWgs84());
    servicePointVersionRepository.save(servicePointVersion);

    final List<ServicePointVersion> all = servicePointVersionRepository.findAll();
    assertThat(all).isNotEmpty();
  }

  @Test
  void findAllByCoordinates() {
    final List<ServicePointGeoData> servicePoints = repository
        .findAll(coordinatesBetween(SpatialReference.WGS84WEB, ENVELOPE_WITHOUT_DATA)
            .or(coordinatesBetween(SpatialReference.LV95, ENVELOPE_WITHOUT_DATA))
            .or(coordinatesBetween(SpatialReference.LV03, ENVELOPE_WITHOUT_DATA))
            .or(coordinatesBetween(SpatialReference.WGS84, ENVELOPE_WITH_DATA))
        );

    assertThat(servicePoints).isNotEmpty();
    assertThat(servicePoints.get(0).getEast()).isEqualTo(0.1D);
    assertThat(servicePoints.get(0).getNorth()).isEqualTo(0.1D);
  }

  @Test
  void findAllByCoordinatesNothingFound() {
    final List<ServicePointGeoData> servicePoints = repository.findAll(
        coordinatesBetween(SpatialReference.WGS84, ENVELOPE_WITHOUT_DATA));

    assertThat(servicePoints).isEmpty();
  }
}
