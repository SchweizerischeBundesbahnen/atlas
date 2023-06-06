package ch.sbb.exportservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServicePointRepository {

  @Autowired
  @Qualifier("servicePointJdbcTemplate")
  private JdbcTemplate jdbcTemplate;

  public int count() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM service_point_version", Integer.class);
  }
}
