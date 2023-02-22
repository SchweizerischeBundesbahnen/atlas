package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LoadingPointVersionRepositoryTest {

  private final LoadingPointVersionRepository loadingPointVersionRepository;

  @Autowired
  public LoadingPointVersionRepositoryTest(LoadingPointVersionRepository loadingPointVersionRepository) {
    this.loadingPointVersionRepository = loadingPointVersionRepository;
  }

  @AfterEach
  void tearDown() {
    loadingPointVersionRepository.deleteAll();
  }

  @Test
  void shouldSaveLoadingPoint() {
    // given
    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(1)
        .designation("Ladestelle")
        .designationLong("Grosse Ladestelle")
        .connectionPoint(true)
        .servicePointNumber(ServicePointNumber.of(85070003))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    LoadingPointVersion savedVersion = loadingPointVersionRepository.save(loadingPointVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.hasGeolocation()).isFalse();
  }

  @Test
  void shouldSaveLoadingPointWithGeoLocation() {
    // given
    LoadingPointGeolocation loadingPointGeolocation = LoadingPointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600037.945)
        .north(1199749.812)
        .height(2540.21)
        .build();

    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(1)
        .designation("Ladestelle")
        .designationLong("Grosse Ladestelle")
        .connectionPoint(true)
        .servicePointNumber(ServicePointNumber.of(85070003))
        .loadingPointGeolocation(loadingPointGeolocation)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    LoadingPointVersion savedVersion = loadingPointVersionRepository.save(loadingPointVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.hasGeolocation()).isTrue();
    assertThat(savedVersion.getLoadingPointGeolocation().getId()).isNotNull();
  }

}
