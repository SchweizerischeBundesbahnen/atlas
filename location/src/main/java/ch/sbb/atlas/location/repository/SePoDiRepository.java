package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SePoDiRepository {

  @Qualifier("sePoDiJdbcTemplate")
  private final JdbcTemplate sePoDiJdbcTemplate;

  public Set<String> getAlreadyDistributedSloid(SloidType sloidType) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(sePoDiJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    if(SloidType.AREA == sloidType){
      mapSqlParameterSource.addValue("traffic_point_element_type", TrafficPointElementType.BOARDING_AREA.name());
    }else {
      mapSqlParameterSource.addValue("traffic_point_element_type", TrafficPointElementType.BOARDING_PLATFORM.name());
    }
    String sqlQuery = "select distinct sloid from traffic_point_element_version where sloid is not null and "
        + "traffic_point_element_type = :traffic_point_element_type";
    return new HashSet<>(namedParameterJdbcTemplate.query(sqlQuery,mapSqlParameterSource,
        (rs, rowNum) -> rs.getString("sloid")
    ));
  }

  public Set<String> getAlreadyServicePointDistributedSloid() {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(sePoDiJdbcTemplate);
    String sqlQuery = "select distinct sloid from service_point_version where sloid is not null";
    return new HashSet<>(namedParameterJdbcTemplate.query(sqlQuery,
        (rs, rowNum) -> rs.getString("sloid")
    ));
  }

}
