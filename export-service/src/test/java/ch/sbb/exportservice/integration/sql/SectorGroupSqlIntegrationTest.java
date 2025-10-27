package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.exportservice.job.sepodi.sectorgroup.sql.SectorGroupSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sectorgroup.sql.SectorGroupVersionRowMapper;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SectorGroupSqlIntegrationTest extends BaseSqlIntegrationTest {

  @Test
  void shouldReturnFullSectorGroups() throws SQLException {
    //given
    SectorGroupVersion expectedSectorGroupVersion = SectorGroupVersion.builder()
        .id(1L)
        .sloid("ch:1:sloid:8000:1:100")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2001, 1, 1))
        .length(150.000)
        .trafficPointSloid("ch:1:sloid:6602:0:7110")
        .designation("test1")
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .creator("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .editionDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .editor("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .version(0)
        .build();

    insertSectorGroup("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));
    String sqlQuery = SectorGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<SectorGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedSectorGroupVersion);

  }

  @Test
  void shouldReturnActualSectorGroup() throws SQLException {
    SectorGroupVersion expectedSectorGroupVersion = SectorGroupVersion.builder()
        .id(2L)
        .sloid("ch:1:sloid:8000:1:200")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusMonths(2))
        .length(150.000)
        .trafficPointSloid("ch:1:sloid:6602:0:7110")
        .designation("test1")
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .creator("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .editionDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .editor("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .version(0)
        .build();

    //given
    insertSectorGroup("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));
    insertSectorGroup("ch:1:sloid:8000:1:200", LocalDate.now(), LocalDate.now().plusMonths(2));

    String sqlQuery = SectorGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<SectorGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedSectorGroupVersion);

  }

  @Test
  void shouldReturnTimetableFutureSectorGroup() throws SQLException {
    DateRange timetableYearsDateRange = ExportYearsTimetableUtil.getTimetableYearsDateRange();

    SectorGroupVersion expectedSectorGroupVersion = SectorGroupVersion.builder()
        .id(2L)
        .sloid("ch:1:sloid:8000:1:200")
        .validFrom(timetableYearsDateRange.getFrom().plusMonths(1))
        .validTo(timetableYearsDateRange.getTo().minusMonths(1))
        .length(150.000)
        .trafficPointSloid("ch:1:sloid:6602:0:7110")
        .designation("test1")
        .status(Status.VALIDATED)
        .creationDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .creator("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .editionDate(LocalDateTime.of(2025, 9, 9, 11, 6, 36, 541_447_000))
        .editor("abab81fb-6ba0-4153-af93-8fb3dc910210")
        .version(0)
        .build();

    //given
    insertSectorGroup("ch:1:sloid:8000:1:100", LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1));

    insertSectorGroup("ch:1:sloid:8000:1:200", timetableYearsDateRange.getFrom().plusMonths(1),
        timetableYearsDateRange.getTo().minusMonths(1));

    String sqlQuery = SectorGroupSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    //when
    List<SectorGroupVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedSectorGroupVersion);

  }

  protected void insertSectorGroup(String sloid, LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO sector_group_version (id, sloid, traffic_point_sloid, valid_from, valid_to, designation, length, status, creation_date,
                                    creator, edition_date, editor, version)
        VALUES (nextval('sector_group_version_seq'), '%s', 'ch:1:sloid:6602:0:7110', '%s', '%s', 'test1', 150.000, 'VALIDATED', '2025-09-09 11:06:36.541447',
                'abab81fb-6ba0-4153-af93-8fb3dc910210', '2025-09-09 11:06:36.541447', 'abab81fb-6ba0-4153-af93-8fb3dc910210', 0);
        """
        .formatted(sloid, formatDate(validFrom), formatDate(validTo));
    final Connection connection = servicePointDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  private List<SectorGroupVersion> executeQuery(String sqlQuery) throws SQLException {
    List<SectorGroupVersion> result = new ArrayList<>();
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      SectorGroupVersionRowMapper sectorGroupVersionRowMapper = new SectorGroupVersionRowMapper();
      while (resultSet.next()) {
        SectorGroupVersion sectorVersion = sectorGroupVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(sectorVersion);
      }
    }
    connection.close();
    return result;
  }
}
