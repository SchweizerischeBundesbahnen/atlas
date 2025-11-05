package ch.sbb.exportservice.job;

import static ch.sbb.exportservice.model.ExportTypeV2.ACTUAL;
import static ch.sbb.exportservice.model.ExportTypeV2.FULL;
import static ch.sbb.exportservice.model.ExportTypeV2.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.model.ExportTypeV2.WORLD_FULL;
import static ch.sbb.exportservice.model.ExportTypeV2.WORLD_FUTURE_TIMETABLE;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

public abstract class SqlQueryUtil {

  public static String getFromStatementQueryForWorldOnlyTypes(ExportTypeV2 exportTypeV2, String fromStatement) {
    LocalDate exportDate = LocalDate.now();
    if (exportTypeV2.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(fromStatement, DateHelper.getDateAsSqlString(exportDate), DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClauseForWorldOnlyTypes(ExportTypeV2 exportTypeV2, String whereStatement) {
    if (exportTypeV2.equals(WORLD_FULL)) {
      return "";
    }
    LocalDate exportDate = LocalDate.now();
    if (exportTypeV2.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(whereStatement, DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClause(ExportTypeV2 exportTypeV2, String whereStatement) {
    if (exportTypeV2.equals(FULL)) {
      return "";
    }
    if (exportTypeV2.equals(ACTUAL)) {
      return String.format(whereStatement, DateHelper.getDateAsSqlString(LocalDate.now()));
    }
    if (exportTypeV2.equals(FUTURE_TIMETABLE)) {
      LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
      return String.format(whereStatement, DateHelper.getDateAsSqlString(futureTimeTableYearDate));
    }
    throw new IllegalArgumentException("Value not allowed: " + exportTypeV2);
  }

  public static String buildSqlQuery(String... parts) {
    return String.join(" ", parts) + ";";
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
        case FUTURE_TIMETABLE, WORLD_FUTURE_TIMETABLE, SWISS_FUTURE_TIMETABLE -> {
          String futureTimetable = DateHelper.getDateAsSqlString(
              FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
          String sqlCondition = "'%s' >= " + validFromIdentifier + " AND '%s' <= " + validToIdentifier;
          yield sqlCondition.formatted(futureTimetable, futureTimetable);
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
