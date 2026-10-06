package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class ServicePointDataSourceConfigurationTest extends BaseSqlIntegrationTest {

  @Autowired
  @Qualifier("servicePointJdbcTemplate")
  private NamedParameterJdbcTemplate servicePointJdbcTemplate;

  @Test
  void shouldProvideServicePointJdbcTemplate() {
    // Given

    // When

    // Then
    assertThat(servicePointJdbcTemplate).isNotNull();
  }
}
