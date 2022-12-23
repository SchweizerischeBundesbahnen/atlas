package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class ServicePointNumberRepositoryTest {

  private final ServicePointNumberRepository servicePointNumberRepository;

  @Autowired
  public ServicePointNumberRepositoryTest(
      ServicePointNumberRepository servicePointNumberRepository) {
    this.servicePointNumberRepository = servicePointNumberRepository;
  }

  @AfterEach
  void tearDown() {
    servicePointNumberRepository.deleteAll();
  }

  @Test
  void shouldSaveLoadingPoint() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber
        .builder()
        .number(85070003)
        .used(true)
        .country(Country.SWITZERLAND)
        .build();

    // when
    ServicePointNumber saved = servicePointNumberRepository.save(servicePointNumber);

    // then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getNumber()).isEqualTo(85070003);
    assertThat(saved.isUsed()).isTrue();
    assertThat(saved.getCountry()).isEqualTo(Country.SWITZERLAND);
  }
}
