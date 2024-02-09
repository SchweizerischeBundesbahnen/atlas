package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrmRepository {

  @Qualifier("prmJdbcTemplate")
  private final NamedParameterJdbcTemplate prmJdbcTemplate;

  public Set<String> getAlreadyDistributedSloids(SloidType sloidType) {
    String entityName = getEntityName(sloidType);
    String sqlQuery = "select distinct sloid from " + entityName + " where sloid is not null;";
    return new HashSet<>(prmJdbcTemplate.query(sqlQuery,
        (rs, rowNum) -> rs.getString("sloid")
    ));
  }

  private String getEntityName(SloidType sloidType) {
    return switch (sloidType) {
      case PLATFORM, AREA, SERVICE_POINT ->
          throw new IllegalArgumentException("Wrong sloidType " + sloidType + " provided! Please"
              + " use only PRM SloidTypes!");
      case CONTACT_POINT -> "contact_point_version";
      case PARKING_LOT -> "parking_lot_version";
      case REFERENCE_POINT -> "reference_point_version";
      case TOILET -> "toilet_version";
    };
  }

}
