package ch.sbb.atlas.location.repository;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SePoDiRepository {

  @Qualifier("sePoDiJdbcTemplate")
  private final JdbcTemplate sePoDiJdbcTemplate;

  public Set<String> getServicePointSloid() {
    return new HashSet<>(sePoDiJdbcTemplate.queryForList("""
        select distinct (sloid)
        from service_point_version where sloid is not null
        """, String.class));
  }

}
