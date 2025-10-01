package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorWithGroupVersion;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorWithGroupSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorWithGroupVersionRowMapper;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SectorWithGroupSqlIntegrationTest extends BaseSqlIntegrationTest {

  @Test
  void shouldReturnFullSectorsWithGroups() throws SQLException {
    //given
    insertSectorWithGroupRelation("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));
    String sqlQuery = SectorWithGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<SectorWithGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(2);

  }

  @Test
  void shouldReturnActualSectors() throws SQLException {
    //given
    insertSectorWithGroupRelation("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));

    insertSectorWithGroupRelation("ch:1:sloid:8000:1:200", LocalDate.now(), LocalDate.now().plusMonths(2));

    String sqlQuery = SectorWithGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<SectorWithGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(2);

  }

  @Test
  void shouldReturnTimetableFutureSectors() throws SQLException {
    //given
    DateRange timetableYearsDateRange = ExportYearsTimetableUtil.getTimetableYearsDateRange();
    insertSectorWithGroupRelation("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));

    insertSectorWithGroupRelation("ch:1:sloid:8000:1:200", timetableYearsDateRange.getFrom().plusMonths(1),
        timetableYearsDateRange.getTo().minusMonths(1));

    String sqlQuery = SectorWithGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    //when
    List<SectorWithGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(2);

  }

  protected void insertSectorWithGroupRelation(String sloid, LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO sector_version (id, sloid, traffic_point_sloid, valid_from, valid_to, designation, north,
                                    east, height, spatial_reference, length, edge_height, creation_date,
                                    creator, edition_date, editor, version)
        VALUES (nextval('sector_version_seq'), '%s', 'ch:1:sloid:6602:0:7110', '%s', '%s', 'test1', 225738.00000000000,
                681821.00000000000, 540.20000, 'LV95', 150.000, 120, '2025-09-09 11:06:36.541447',
                'abab81fb-6ba0-4153-af93-8fb3dc910210', '2025-09-09 11:06:36.541447', 'abab81fb-6ba0-4153-af93-8fb3dc910210', 0);
        INSERT INTO sector_group_version (id, sloid, traffic_point_sloid, valid_from, valid_to, designation, creation_date,
                                    creator, edition_date, editor, version)
        VALUES (nextval('sector_group_version_seq'), '%s1', 'ch:1:sloid:6602:0:7110', '%s', '%s', 'Group', '2025-09-09 11:06:36.541447',
                'abab81fb-6ba0-4153-af93-8fb3dc910210', '2025-09-09 11:06:36.541447', 'abab81fb-6ba0-4153-af93-8fb3dc910210', 0);
        INSERT INTO sector_group_relations (sector_sloid, sector_group_sloid) VALUES ('%s', '%s1');
        """
        .formatted(sloid, formatDate(validFrom), formatDate(validTo),
            sloid, formatDate(validFrom), formatDate(validTo),
            sloid, sloid);
    final Connection connection = servicePointDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  private List<SectorWithGroupVersion> executeQuery(String sqlQuery) throws SQLException {
    List<SectorWithGroupVersion> result = new ArrayList<>();
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      SectorWithGroupVersionRowMapper sectorVersionRowMapper = new SectorWithGroupVersionRowMapper();
      while (resultSet.next()) {
        SectorWithGroupVersion sectorVersion = sectorVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(sectorVersion);
      }
    }
    connection.close();
    return result;
  }

}
