package ch.sbb.exportservice.job;

import ch.sbb.atlas.helper.DateHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

public abstract class SqlQueryUtil {

  public static String buildSqlQuery(String... parts) {
    return String.join(" ", parts) + ";";
  }

  public static String buildSqlQuery(String selectStatement, String groupByAndOrderByClause, ExportTypeV2 exportTypeV2) {
    return ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .selectStatement(selectStatement)
        .groupByAndOrderByClause(groupByAndOrderByClause)
        .build().getQuery();
  }

  @Data
  @Builder
  protected static class ExportSqlQueryBuilder {

    private final String selectStatement;
    private final String whereClause;
    private final String groupByAndOrderByClause;
    @Builder.Default
    private String validFromIdentifier = "valid_from";
    @Builder.Default
    private String validToIdentifier = "valid_to";

    private final ExportTypeV2 exportType;

    public String getQuery() {
      return buildSqlQuery(selectStatement, buildWhereClause(), groupByAndOrderByClause);
    }

    private String buildWhereClause() {
      if (whereClause == null) {
        return "WHERE " + buildTypeCondition();
      }
      return whereClause + " AND " + buildTypeCondition();
    }

    private String buildTypeCondition() {
      return switch (exportType) {
        case FULL, WORLD_FULL, SWISS_FULL -> "1=1";
        case ACTUAL, WORLD_ACTUAL, SWISS_ACTUAL -> {
          String today = DateHelper.getDateAsSqlString(LocalDate.now());
          String sqlCondition = "'%s' >= " + validFromIdentifier + " AND '%s' <= " + validToIdentifier;
          yield sqlCondition.formatted(today, today);
        }
        case TIMETABLE_YEARS, WORLD_TIMETABLE_YEARS, SWISS_TIMETABLE_YEARS -> {
          DateRange timetableYearsDateRange = ExportYearsTimetableUtil.getTimetableYearsDateRange();
          String timetableYearsStart = DateHelper.getDateAsSqlString(timetableYearsDateRange.getFrom());
          String timetableYearsEnd = DateHelper.getDateAsSqlString(timetableYearsDateRange.getTo());
          String sqlCondition = "'%s' <= " + validToIdentifier + "  AND " + validFromIdentifier + " <= '%s'";
          yield sqlCondition.formatted(timetableYearsStart, timetableYearsEnd);
        }
      };
    }
  }
}
