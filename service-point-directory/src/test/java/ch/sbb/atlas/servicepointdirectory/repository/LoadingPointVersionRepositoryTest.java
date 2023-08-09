package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import java.time.LocalDate;
import java.util.List;
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
  }

  @Test
  void shouldFindAllByServicePointNumberAndNumberOrderByValidFrom() {
    // given
    final List<LoadingPointVersion> givenVersions = List.of(
        LoadingPointVersion
            .builder()
            .number(1)
            .servicePointNumber(ServicePointNumber.of(90070001))
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .designation("Ladestelle")
            .build(),
        LoadingPointVersion
            .builder()
            .number(1)
            .servicePointNumber(ServicePointNumber.of(85070001))
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .designation("Ladestelle")
            .build(),
        LoadingPointVersion
            .builder()
            .number(5)
            .servicePointNumber(ServicePointNumber.of(85070001))
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .designation("Ladestelle")
            .build(),
        LoadingPointVersion
            .builder()
            .number(1)
            .servicePointNumber(ServicePointNumber.of(85070001))
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .designation("Ladestelle")
            .build()
    );
    loadingPointVersionRepository.saveAll(givenVersions);

    // when
    final List<LoadingPointVersion> foundLoadingPoints =
        loadingPointVersionRepository.findAllByServicePointNumberAndNumberOrderByValidFrom(
            ServicePointNumber.of(85070001),
            1
        );

    // then
    assertThat(foundLoadingPoints).hasSize(2);
    assertThat(foundLoadingPoints.get(0).getValidFrom()).isEqualTo("2020-01-01");
    assertThat(foundLoadingPoints.get(0).getNumber()).isEqualTo(1);
    assertThat(foundLoadingPoints.get(0).getServicePointNumber().asString()).isEqualTo("85070001");

    assertThat(foundLoadingPoints.get(1).getValidFrom()).isEqualTo("2022-01-01");
    assertThat(foundLoadingPoints.get(1).getNumber()).isEqualTo(1);
    assertThat(foundLoadingPoints.get(1).getServicePointNumber().asString()).isEqualTo("85070001");
  }

  @Test
  void shouldExistByServicePointNumberAndNumber() {
    // given
    final LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(1)
        .servicePointNumber(ServicePointNumber.of(85070001))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Ladestelle")
        .build();
    loadingPointVersionRepository.save(loadingPointVersion);

    // when
    final boolean result = loadingPointVersionRepository.existsByServicePointNumberAndNumber(
        ServicePointNumber.of(85070001),
        1
    );

    // then
    assertThat(result).isTrue();
  }

}
