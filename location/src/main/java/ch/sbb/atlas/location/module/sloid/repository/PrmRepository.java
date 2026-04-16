package ch.sbb.atlas.location.module.sloid.repository;

import ch.sbb.atlas.api.location.SloidType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrmRepository {

  private static final String QUERY_TEMPLATE = "select distinct sloid from %s where sloid is not null;";

  private static final Map<SloidType, String> ALLOWED_TABLE_NAMES = Map.of(
      SloidType.CONTACT_POINT, "contact_point_version",
      SloidType.PARKING_LOT, "parking_lot_version",
      SloidType.REFERENCE_POINT, "reference_point_version",
      SloidType.TOILET, "toilet_version"
  );

  @Qualifier("prmJdbcTemplate")
  private final NamedParameterJdbcTemplate prmJdbcTemplate;

  public Set<String> getAlreadyDistributedSloids(SloidType sloidType) {
    String tableName = ALLOWED_TABLE_NAMES.get(sloidType);
    if (tableName == null) {
      throw new IllegalArgumentException("Wrong sloidType " + sloidType + " provided! Please"
          + " use only PRM SloidTypes!");
    }
    String sqlQuery = String.format(QUERY_TEMPLATE, tableName);
    return new HashSet<>(prmJdbcTemplate.query(sqlQuery,
        (rs, rowNum) -> rs.getString("sloid")
    ));
  }

}
