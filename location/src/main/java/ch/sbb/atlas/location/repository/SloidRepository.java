package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.Country;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SloidRepository {

  private static final String AREA_SEQ = "area_seq";
  private static final String EDGE_SEQ = "edge_seq";

  @Qualifier("locationJdbcTemplate")
  private final NamedParameterJdbcTemplate locationJdbcTemplate;

  public SloidRepository(NamedParameterJdbcTemplate locationJdbcTemplate) {
    this.locationJdbcTemplate = locationJdbcTemplate;
  }

  public Set<String> getAllocatedSloid(SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "select distinct sloid from allocated_sloid where sloid is not null and sloidType = :sloidType";
    return new HashSet<>(locationJdbcTemplate.query(sqlQuery, mapSqlParameterSource, (rs, row) -> rs.getString("sloid")));
  }

  public Integer getNextSeqValue(SloidType sloidType) {
    final String sequence = sloidType == SloidType.PLATFORM ? EDGE_SEQ : AREA_SEQ;
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sequence", sequence);
    String sqlQuery = "select nextval(:sequence);";
    return locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource, (rs, row) -> rs.getInt(1));
  }

  public void insertSloid(String sloid, SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "insert into allocated_sloid (sloid,sloidType) values (:sloid,:sloidType);";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public String getNextAvailableSloid(Country country) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("country", country.name());
    String sqlQuery = "select sloid from available_service_point_sloid where country = :country and claimed = false order by "
        + "sloid limit 1;";
    return locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource, (rs, row) -> rs.getString("sloid"));
  }

  public boolean isSloidAllocated(String sloid) {
    Byte nbOfFoundSloids = locationJdbcTemplate.getJdbcTemplate()
        .queryForObject("select count(*) from allocated_sloid where sloid = ?;",Byte.class,sloid);
    if (nbOfFoundSloids == null) {
      throw new IllegalStateException("select count query should not return null!");
    }
    return nbOfFoundSloids == 1;
  }

  public void deleteAllocatedSloid(Set<String> sloids, SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "delete from allocated_sloid where sloid in (:sloids) and sloidType = :sloidType";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void deleteAvailableServicePointSloidAlreadyClaimed(Set<String> sloids) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "delete from available_service_point_sloid where sloid in (:sloids) and claimed = true";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidToUnclaimedAllocatedSloid(Set<String> sloids) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "update available_service_point_sloid set claimed = false where sloid in (:sloids)";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidToClaimed(String sloid) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    String sqlQuery = "update available_service_point_sloid set claimed = true where sloid = :sloid;";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidToClaimed(Set<String> sloids) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "update available_service_point_sloid set claimed = true where sloid in (:sloids)";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void addMissingAllocatedSloid(Set<String> sloidToAdd, SloidType sloidType) {
    ArrayList<String> sloids = new ArrayList<>(sloidToAdd);
    String sqlQuery = "insert into allocated_sloid (sloid,sloidType) values (?,?)";
    locationJdbcTemplate.getJdbcTemplate().batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setString(1, sloids.get(i));
            ps.setString(2, sloidType.name());
          }

          @Override
          public int getBatchSize() {
            return sloids.size();
          }
        }
    );
  }
}
