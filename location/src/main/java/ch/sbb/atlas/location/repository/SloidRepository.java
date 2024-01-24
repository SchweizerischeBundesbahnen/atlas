package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.Country;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SloidRepository {

  private static final String AREA_SEQ = "area_seq";
  private static final String EDGE_SEQ = "edge_seq";

  //TODO: use NamedParameterJdbcTemplate
  @Qualifier("locationJdbcTemplate")
  private final JdbcTemplate locationJdbcTemplate;

  public Set<String> getAllocatedSloid(SloidType sloidType) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(locationJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "select distinct sloid from allocated_sloid where sloid is not null and sloidType = :sloidType";
    return new HashSet<>(namedParameterJdbcTemplate.query(sqlQuery, mapSqlParameterSource, (rs, row) -> rs.getString("sloid")));
  }

  public Integer getNextSeqValue(SloidType sloidType) {
    final String sequence = sloidType == SloidType.PLATFORM ? EDGE_SEQ : AREA_SEQ;
    return locationJdbcTemplate.queryForObject("select nextval(?);", Integer.class, sequence);
  }

  public void insertSloid(String sloid, SloidType sloidType) {
    locationJdbcTemplate.update("insert into allocated_sloid (sloid,sloidType) values (?,?);", sloid, sloidType.name());
  }

  public String getNextAvailableSloid(Country country) {
    return locationJdbcTemplate.queryForObject(
        "select sloid from available_service_point_sloid where country = ? and claimed = false order by sloid limit 1;",
        String.class, country.name());
  }

  public boolean isSloidAvailable(String sloid) {
    try {
      Boolean claimed = locationJdbcTemplate.queryForObject("select claimed from available_service_point_sloid where sloid = ?;",
          Boolean.class,
          sloid);
      return Boolean.FALSE.equals(claimed);
    } catch (DataAccessException e) {
      return false;
    }
  }

  public int deleteAllocatedSloid(Set<String> sloids, SloidType sloidType) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(locationJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "delete from allocated_sloid where sloid in (:sloids) and sloidType = :sloidType";
    return namedParameterJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public int deleteAvailableServicePointSloidAlreadyClaimed(Set<String> sloids) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(locationJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "delete from available_service_point_sloid where sloid in (:sloids) and claimed = true";
    return namedParameterJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public int setAvailableSloidToUnclaimedAllocatedSloid(Set<String> sloids) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(locationJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "update available_service_point_sloid set claimed = false where sloid in (:sloids)";
    return namedParameterJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public int setAvailableSloidToUnsed(String sloid, Country country) {
    return locationJdbcTemplate.update("update available_service_point_sloid set claimed = false where sloid = ? and country = "
            + "?;",
        sloid,
        country.name());
  }

  public int setAvailableSloidToClaimed(String sloid) {
    return locationJdbcTemplate.update("update available_service_point_sloid set claimed = true where sloid = ?;", sloid);
  }

  public int setAvailableSloidToClaimed(Set<String> sloids) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(locationJdbcTemplate);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "update available_service_point_sloid set claimed = true where sloid in (:sloids)";
    return namedParameterJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void addMissingAllocatedSloid(Set<String> sloidToAdd, SloidType sloidType) {
    ArrayList<String> sloids = new ArrayList<>(sloidToAdd);
    String sqlQuery = "insert into allocated_sloid (sloid,sloidType) values (?,?)";
    locationJdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
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
