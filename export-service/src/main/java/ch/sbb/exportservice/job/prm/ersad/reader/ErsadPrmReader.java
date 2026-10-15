package ch.sbb.exportservice.job.prm.ersad.reader;

import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.contactpoint.sql.ContactPointVersionRowMapper;
import ch.sbb.exportservice.job.prm.contactpoint.sql.ContactPointVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.parkinglot.sql.ParkingLotVersionRowMapper;
import ch.sbb.exportservice.job.prm.parkinglot.sql.ParkingLotVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionRowMapper;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.referencepoint.entity.ReferencePointVersion;
import ch.sbb.exportservice.job.prm.referencepoint.sql.ReferencePointVersionRowMapper;
import ch.sbb.exportservice.job.prm.referencepoint.sql.ReferencePointVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.relation.sql.RelationVersionRowMapper;
import ch.sbb.exportservice.job.prm.relation.sql.RelationVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionRowMapper;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import ch.sbb.exportservice.job.prm.toilet.sql.ToiletVersionRowMapper;
import ch.sbb.exportservice.job.prm.toilet.sql.ToiletVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErsadPrmReader {

  private static final int CHUNK_SIZE = 500;

  private final NamedParameterJdbcTemplate prmJdbcTemplate;

  public Map<String, StopPointVersion> readValidatedTrainStopPoints() {
    String query = withAdditionalCondition(
        StopPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "spv.status = 'VALIDATED' AND EXISTS (SELECT 1 FROM stop_point_version_means_of_transport mot " +
            "WHERE mot.stop_point_version_id = spv.id AND mot.means_of_transport = 'TRAIN')");
    List<StopPointVersion> stopPoints = prmJdbcTemplate.query(query, new StopPointVersionRowMapper());
    return stopPoints.stream().collect(LinkedHashMap::new, (map, stopPoint) -> map.put(stopPoint.getSloid(), stopPoint), Map::putAll);
  }

  public Map<String, List<PlatformVersion>> readPlatforms(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        PlatformVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "pv.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new PlatformVersionRowMapper(),
        PlatformVersion::getParentServicePointSloid);
  }

  public Map<String, List<ReferencePointVersion>> readReferencePoints(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        ReferencePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "rpv.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new ReferencePointVersionRowMapper(),
        ReferencePointVersion::getParentServicePointSloid);
  }

  public Map<String, List<RelationVersion>> readRelations(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        RelationVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "rv.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new RelationVersionRowMapper(),
        RelationVersion::getParentServicePointSloid);
  }

  public Map<String, List<ContactPointVersion>> readContactPoints(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        ContactPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "cpv.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new ContactPointVersionRowMapper(),
        ContactPointVersion::getParentServicePointSloid);
  }

  public Map<String, List<ToiletVersion>> readToilets(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        ToiletVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "toi.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new ToiletVersionRowMapper(),
        ToiletVersion::getParentServicePointSloid);
  }

  public Map<String, List<ParkingLotVersion>> readParkingLots(Set<String> parentSloids) {
    return readGroupedByParentSloid(
        ParkingLotVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL),
        "plv.parent_service_point_sloid IN (:sloids)",
        parentSloids,
        new ParkingLotVersionRowMapper(),
        ParkingLotVersion::getParentServicePointSloid);
  }

  private <T> Map<String, List<T>> readGroupedByParentSloid(String sqlQuery, String additionalCondition, Set<String> parentSloids,
      RowMapper<T> rowMapper, ParentExtractor<T> parentExtractor) {
    if (parentSloids.isEmpty()) {
      return Map.of();
    }
    String filteredQuery = withAdditionalCondition(sqlQuery, additionalCondition);
    Map<String, List<T>> grouped = new LinkedHashMap<>();
    for (List<String> chunk : chunk(parentSloids)) {
      MapSqlParameterSource params = new MapSqlParameterSource("sloids", chunk);
      List<T> rows = prmJdbcTemplate.query(filteredQuery, params, rowMapper);
      rows.forEach(row -> grouped.computeIfAbsent(parentExtractor.getParentSloid(row), key -> new ArrayList<>()).add(row));
    }
    return grouped;
  }

  private static String withAdditionalCondition(String sqlQuery, String additionalCondition) {
    String normalized = trimTrailingSemicolon(sqlQuery);
    int groupByIndex = normalized.toLowerCase().indexOf(" group by ");
    String beforeGroupBy = normalized.substring(0, groupByIndex);
    String groupBy = normalized.substring(groupByIndex);
    return beforeGroupBy + " AND " + additionalCondition + " " + groupBy + ";";
  }

  private static List<List<String>> chunk(Set<String> values) {
    List<String> allValues = new ArrayList<>(new LinkedHashSet<>(values));
    List<List<String>> chunks = new ArrayList<>();
    for (int index = 0; index < allValues.size(); index += CHUNK_SIZE) {
      int end = Math.min(index + CHUNK_SIZE, allValues.size());
      chunks.add(allValues.subList(index, end));
    }
    return chunks;
  }

  private static String trimTrailingSemicolon(String sqlQuery) {
    String trimmed = sqlQuery.trim();
    if (trimmed.endsWith(";")) {
      return trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed;
  }

  @FunctionalInterface
  private interface ParentExtractor<T> {

    String getParentSloid(T value);
  }
}
