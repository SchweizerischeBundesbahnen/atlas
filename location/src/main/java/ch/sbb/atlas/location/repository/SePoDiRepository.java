package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SePoDiRepository {

  private static final String TRAFFIC_POINT_ELEMENT_TYPE = "traffic_point_element_type";
  private static final String SLOID = "sloid";

  @Qualifier("sePoDiJdbcTemplate")
  private final NamedParameterJdbcTemplate sePoDiJdbcTemplate;

  public SePoDiRepository(NamedParameterJdbcTemplate sePoDiJdbcTemplate) {
    this.sePoDiJdbcTemplate = sePoDiJdbcTemplate;
  }

  public Set<String> getAlreadyDistributedSloids(SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    if (SloidType.AREA == sloidType) {
      mapSqlParameterSource.addValue(TRAFFIC_POINT_ELEMENT_TYPE, TrafficPointElementType.BOARDING_AREA.name());
    } else {
      mapSqlParameterSource.addValue(TRAFFIC_POINT_ELEMENT_TYPE, TrafficPointElementType.BOARDING_PLATFORM.name());
    }
    String sqlQuery = """
        select distinct sloid from traffic_point_element_version
        where sloid is not null and traffic_point_element_type = :traffic_point_element_type;
        """;
    return new HashSet<>(sePoDiJdbcTemplate.query(sqlQuery, mapSqlParameterSource,
        (rs, rowNum) -> rs.getString(SLOID)
    ));
  }

  public Set<String> getAlreadyDistributedServicePointSloids() {
    String sqlQuery = "select distinct sloid from service_point_version where sloid is not null;";
    return new HashSet<>(sePoDiJdbcTemplate.query(sqlQuery,
        (rs, rowNum) -> rs.getString(SLOID)
    ));
  }

}
