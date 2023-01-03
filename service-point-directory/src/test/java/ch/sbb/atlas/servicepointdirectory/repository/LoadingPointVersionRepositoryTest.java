package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LoadingPointVersionRepositoryTest {

  private final LoadingPointRepository loadingPointRepository;

  @Autowired
  public LoadingPointVersionRepositoryTest(LoadingPointRepository loadingPointRepository) {
    this.loadingPointRepository = loadingPointRepository;
  }

  @AfterEach
  void tearDown() {
    loadingPointRepository.deleteAll();
  }

  @Test
  void shouldSaveLoadingPoint() {
    // given
    LoadingPointGeolocation loadingPointGeolocation = LoadingPointGeolocation
        .builder()
        .locationTypes(LocationTypes
            .builder()
            .spatialReference(SpatialReference.LV95)
            .lv03east(600037.945)
            .lv03north(199749.812)
            .lv95east(2600037.945)
            .lv95north(1199749.812)
            .wgs84east(7.439130891)
            .wgs84north(46.948832291)
            .wgs84webEast(691419.90336)
            .wgs84webNorth(5811120.06939)
            .height(2540.21)
            .build())
        .build();

    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(1)
        .designation("Ladestelle")
        .designationLong("Grosse Ladestelle")
        .connectionPoint(true)
        .servicePointNumber(1)
        .loadingPointGeolocation(loadingPointGeolocation)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    LoadingPointVersion savedVersion = loadingPointRepository.save(loadingPointVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.hasGeolocation()).isTrue();
    assertThat(savedVersion.getLoadingPointGeolocation().getId()).isNotNull();
  }

}
