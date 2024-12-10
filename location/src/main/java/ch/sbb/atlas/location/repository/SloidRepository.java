package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.model.AtlasListUtil;
import ch.sbb.atlas.servicepoint.Country;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SloidRepository {

  private static final String AREA_SEQ = "area_seq";
  private static final String EDGE_SEQ = "edge_seq";
  public static final int BATCH_SIZE = 5_000;

  @Qualifier("locationJdbcTemplate")
  private final NamedParameterJdbcTemplate locationJdbcTemplate;

  public SloidRepository(NamedParameterJdbcTemplate locationJdbcTemplate) {
    this.locationJdbcTemplate = locationJdbcTemplate;
  }

  public Set<String> getAllocatedSloids(SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "select distinct sloid from allocated_sloid where sloid is not null and "
        + "sloidtype = :sloidType;";
    return new HashSet<>(locationJdbcTemplate.query(sqlQuery, mapSqlParameterSource,
        (rs, row) -> rs.getString("sloid")));
  }

  public Integer getNextSeqValue(SloidType sloidType) {
    final String sequence = sloidType == SloidType.PLATFORM ? EDGE_SEQ : AREA_SEQ;
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sequence", sequence);
    String sqlQuery = "select nextval(:sequence);";
    return locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource,
        (rs, row) -> rs.getInt(1));
  }

  public String insertSloid(String sloid, SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "insert into allocated_sloid (sloid, sloidtype) values (:sloid, :sloidType);";
    try {
      locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
    } catch (DuplicateKeyException e) {
      return null;
    }
    return sloid;
  }

  public String getNextAvailableSloid(Country country) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("country", country.name());
    String sqlQuery = "select sloid from available_service_point_sloid where country = :country "
        + "and claimed = false limit 1;";
    return locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource,
        (rs, row) -> rs.getString("sloid"));
  }

  public boolean isSloidAllocated(String sloid) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    String sqlQuery = "select count(*) from allocated_sloid where sloid = :sloid;";
    Byte nbOfFoundSloids = locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource,
        (rs, row) -> rs.getByte(1));
    if (nbOfFoundSloids == null) {
      throw new IllegalStateException("select count query should not return null!");
    }
    return nbOfFoundSloids == 1;
  }

  public boolean isServicePointSloidAvailable(String sloid) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    String sqlQuery = "select claimed from available_service_point_sloid where sloid = :sloid;";
    try {
      Boolean claimed = locationJdbcTemplate.queryForObject(sqlQuery, mapSqlParameterSource,
          (rs, row) -> rs.getBoolean("claimed"));
      return Boolean.FALSE.equals(claimed);
    } catch (DataAccessException e) {
      return false;
    }
  }

  public void deleteAllocatedSloids(Set<String> sloids, SloidType sloidType) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    mapSqlParameterSource.addValue("sloidType", sloidType.name());
    String sqlQuery = "delete from allocated_sloid where sloid in (:sloids) and sloidtype = "
        + ":sloidType;";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidsToUnclaimed(Set<String> sloids) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloids", sloids);
    String sqlQuery = "update available_service_point_sloid set claimed = false where sloid in "
        + "(:sloids);";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidToClaimed(String sloid) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("sloid", sloid);
    String sqlQuery = "update available_service_point_sloid set claimed = true where sloid = "
        + ":sloid;";
    locationJdbcTemplate.update(sqlQuery, mapSqlParameterSource);
  }

  public void setAvailableSloidsToClaimed(Set<String> sloids) {
    log.info("Updating available_service_point_sloid clamed to true...");
    String sqlQuery = "update available_service_point_sloid set claimed = true where sloid in (?);";
    executeBatchUpdate(sloids, sqlQuery);
  }

  public void deleteAvailableServicePointSloidsAlreadyClaimed(Set<String> sloids) {
    log.info("Deleting available_service_point_sloid already claimed...");
    String sqlQuery = "delete from available_service_point_sloid where sloid in (?) and claimed ="
        + " true;";
    executeBatchUpdate(sloids, sqlQuery);
  }

  private void executeBatchUpdate(Set<String> sloids, String sqlQuery) {
    List<String> sloidList = new ArrayList<>(sloids);
    AtlasListUtil.getPartitionedSublists(sloidList, BATCH_SIZE).forEach(sloidSubList -> {
      log.info("Execution batching update: provided list size: {}", sloidSubList.size());
      locationJdbcTemplate.getJdbcTemplate()
                          .batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                  ps.setString(1, sloidSubList.get(i));
                                }

                                @Override
                                public int getBatchSize() {
                                  return sloidSubList.size();
                                }
                              }
                          );
    });
  }

  public void addMissingAllocatedSloids(Set<String> sloidsToAdd, SloidType sloidType) {
    ArrayList<String> sloids = new ArrayList<>(sloidsToAdd);
    String sqlQuery = "insert into allocated_sloid (sloid, sloidtype) values (?, ?);";
    AtlasListUtil.getPartitionedSublists(sloids, BATCH_SIZE).forEach(sloidSubList -> {
      log.info("Execution batching update: provided list size: {}", sloidSubList.size());
      locationJdbcTemplate.getJdbcTemplate()
                          .batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {

                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                  ps.setString(1, sloidSubList.get(i));
                                  ps.setString(2, sloidType.name());
                                }

                                @Override
                                public int getBatchSize() {
                                  return sloidSubList.size();
                                }
                              }
                          );
    });
  }

}
