package ch.sbb.exportservice.job.prm.ersad.reader;

import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.sql.ServicePointVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.servicepoint.sql.ServicePointVersionSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.job.sepodi.trafficpoint.sql.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.trafficpoint.sql.TrafficPointElementVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErsadServicePointReader {

  private static final int CHUNK_SIZE = 500;

  @Qualifier("servicePointJdbcTemplate")
  private final NamedParameterJdbcTemplate servicePointJdbcTemplate;

  public Map<String, ServicePointVersion> readServicePoints(Set<String> sloids) {
    if (sloids.isEmpty()) {
      return Map.of();
    }
    String baseQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);
    String filteredQuery = withAdditionalCondition(baseQuery, "spv.sloid IN (:sloids)");
    Map<String, ServicePointVersion> result = new LinkedHashMap<>();
    for (List<String> chunk : chunk(sloids)) {
      MapSqlParameterSource params = new MapSqlParameterSource("sloids", chunk);
      List<ServicePointVersion> servicePoints = servicePointJdbcTemplate.query(filteredQuery, params, new ServicePointVersionRowMapper());
      servicePoints.forEach(servicePoint -> result.put(servicePoint.getSloid(), servicePoint));
    }
    return result;
  }

  public Map<String, String> readTrafficPointDesignations(Set<String> platformSloids) {
    if (platformSloids.isEmpty()) {
      return Map.of();
    }
    String baseQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);
    String filteredQuery = withAdditionalCondition(baseQuery, "tpev.sloid IN (:sloids)");
    Map<String, String> result = new LinkedHashMap<>();
    for (List<String> chunk : chunk(platformSloids)) {
      MapSqlParameterSource params = new MapSqlParameterSource("sloids", chunk);
      List<TrafficPointElementVersion> elements = servicePointJdbcTemplate.query(filteredQuery, params,
          new TrafficPointElementVersionRowMapper());
      elements.forEach(element -> result.put(element.getSloid(), element.getDesignation()));
    }
    return result;
  }

  private static String withAdditionalCondition(String sqlQuery, String additionalCondition) {
    String normalized = trimTrailingSemicolon(sqlQuery);
    int groupByIndex = normalized.toLowerCase().indexOf("group by");
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
}
