package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@IntegrationTest
class ServicePointNumberRepositoryTest {

  @Autowired
  private ServicePointNumberRepository servicePointNumberRepository;

  @Sql(statements = {"insert into available_service_point_numbers values (123, 'SWITZERLAND')"})
  @Test
  void shouldGetNextAvailableIdByCountry() {
    Integer nextAvailableServicePointNumber = servicePointNumberRepository.getNextAvailableServicePointNumber(
        Country.SWITZERLAND.name());
    assertThat(nextAvailableServicePointNumber).isEqualTo(123);
  }

  @Sql(statements = {"insert into available_service_point_numbers values (123, 'SWITZERLAND')"})
  @Test
  void shouldDeleteNumberAndReturnRowsAffected() {
    Integer result = servicePointNumberRepository.deleteAvailableNumber(123, Country.SWITZERLAND.name());
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldNotDeleteNumberAndReturnRowsAffected() {
    Integer result = servicePointNumberRepository.deleteAvailableNumber(123, Country.SWITZERLAND.name());
    assertThat(result).isZero();
  }
}