package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ContactPointVersion;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.reader.ContactPointVersionRowMapper;
import ch.sbb.exportservice.reader.ContactPointVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactPointSqlIntegrationTest extends BasePrmSqlIntegrationTest {

    @Test
    void shouldReturnFullContactPoints() throws SQLException {
        //given

        insertContactPoint(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(2000, 1, 1),
                LocalDate.of(2099, 12, 31));
        String sqlQuery = ContactPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.FULL);

        //when
        List<ContactPointVersion> result = executeQuery(sqlQuery);

        //then
        assertThat(result).hasSize(1);

    }

    @Test
    void shouldReturnActualContactPoints() throws SQLException {
        //given
        insertContactPoint(2, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.now(),
                LocalDate.now());

        String sqlQuery = ContactPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.ACTUAL);

        //when
        List<ContactPointVersion> result = executeQuery(sqlQuery);

        //then
        assertThat(result).hasSize(1);

    }

    @Test
    void shouldReturnTimetableFutureContactPoints() throws SQLException {
        //given
        LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
        insertContactPoint(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
                actualTimetableYearChangeDate.minusYears(1),
                actualTimetableYearChangeDate.plusYears(1));
        String sqlQuery = ContactPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.TIMETABLE_FUTURE);

        //when
        List<ContactPointVersion> result = executeQuery(sqlQuery);

        //then
        assertThat(result).hasSize(1);

    }

    private List<ContactPointVersion> executeQuery(String sqlQuery) throws SQLException {
        List<ContactPointVersion> result = new ArrayList<>();
        Connection connection = prmDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            assertThat(resultSet).isNotNull();
            ContactPointVersionRowMapper contactPointVersionRowMapper = new ContactPointVersionRowMapper();
            while (resultSet.next()) {
                ContactPointVersion contactPointVersion = contactPointVersionRowMapper.mapRow(resultSet, resultSet.getRow());
                result.add(contactPointVersion);
            }
        }
        connection.close();
        return result;
    }

}
