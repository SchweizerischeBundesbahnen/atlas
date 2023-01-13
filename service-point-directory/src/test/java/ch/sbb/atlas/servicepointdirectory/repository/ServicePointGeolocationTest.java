package ch.sbb.atlas.servicepointdirectory.repository;

import static ch.sbb.atlas.servicepointdirectory.model.TestData.testGeolocation;
import static ch.sbb.atlas.servicepointdirectory.model.TestData.testServicePoint;
import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository.coordinatesBetween;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ServicePointGeolocationTest {

  private final ServicePointVersionRepository repository;

  @Autowired
  public ServicePointGeolocationTest(ServicePointVersionRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void createTestData() {
    final ServicePointVersion servicePointVersion = testServicePoint();
    servicePointVersion.setServicePointGeolocation(testGeolocation());
    repository.save(servicePointVersion);

    List<ServicePointVersion> all = repository.findAll();
    assertThat(all).isNotEmpty();
  }

  @Test
  void findAllByCoordinates() {
    List<ServicePointVersion> servicePoints = repository.findAll(
        coordinatesBetween(0D, 0D, 0.1D, 0.1D));

    assertThat(servicePoints).isNotEmpty();
    assertThat(servicePoints.get(0).getServicePointGeolocation().getEast()).isEqualTo(0.1D);
    assertThat(servicePoints.get(0).getServicePointGeolocation().getNorth()).isEqualTo(0.1D);
  }

  @Test
  void findAllByCoordinatesNothingFound() {
    List<ServicePointVersion> servicePoints = repository.findAll(
        coordinatesBetween(5D, 5D, 10D, 10D));

    assertThat(servicePoints).isEmpty();
  }
}
