package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.reader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.ServicePointVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ServicePointVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest{

    @Test
    public void shouldReturnWorldOnlyActualWithActualBusinessOrganisationData() throws SQLException {
        //given
        LocalDate now = LocalDate.now();
        int servicePointNumber = 19058867;
        insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
        String sboid = "ch:1:sboid:101999";
        insertSharedBusinessOrganisation(sboid,"abb",now,now);
        insertSharedBusinessOrganisation(sboid,"abbIt",now.plusMonths(1),now.plusMonths(2));
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
        assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
        assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isEqualTo("abb");

        deleteSharedBusinessOrganisation(sboid);
        deleteServicePoint(servicePointNumber);
    }

    @Test
    public void shouldReturnWorldOnlyActualWithoutBusinessOrganisationData() throws SQLException {
        //given
        LocalDate now = LocalDate.now();
        int servicePointNumber = 19058867;
        insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
        String sboid = "ch:1:sboid:101999";
        insertSharedBusinessOrganisation(sboid,"abb",now.minusMonths(2),now.minusMonths(1));
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
        assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
        assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isEqualTo(null);

        deleteSharedBusinessOrganisation(sboid);
        deleteServicePoint(servicePointNumber);
    }

    @Test
    public void shouldReturnWorldFullData() throws SQLException {
        //given
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_FULL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(8);
    }

    @Test
    public void shouldReturnWorldOnlyActualData() throws SQLException {
        //given
        LocalDate now = LocalDate.now();
        int servicePointNumber = 19058867;
        insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);

        deleteServicePoint(servicePointNumber);
    }
    @Test
    public void shouldReturnWorldOnlyTimetableFutureData() throws SQLException {
        //given
        LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
        int servicePointNumber = 19058867;
        insertServicePoint(servicePointNumber, now, now, Country.EGYPT);
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_TIMETABLE_FUTURE);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);

        deleteServicePoint(servicePointNumber);
    }

    @Test
    public void shouldReturnSwissOnlyTimetableFutureData() throws SQLException {
        //given
        LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());

        int servicePointNumberAfganistan = 68058867;
        insertServicePoint(servicePointNumberAfganistan, now, now, Country.AFGHANISTAN);
        int servicePointNumberSwitzerland = 85722999;
        insertServicePoint(servicePointNumberSwitzerland, now, now, Country.SWITZERLAND);

        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_TIMETABLE_FUTURE);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumberSwitzerland);

        deleteServicePoint(servicePointNumberAfganistan);
        deleteServicePoint(servicePointNumberSwitzerland);
    }

    @Test
    public void shouldReturnSwissOnlyActualData() throws SQLException {
        //given
        LocalDate now = LocalDate.now();
        int servicePointNumber = 85722999;
        insertServicePoint(servicePointNumber, now, now, Country.SWITZERLAND);
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_ACTUAL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);

        deleteServicePoint(servicePointNumber);
    }

    @Test
    public void shouldReturnSwissOnlyFullData() throws SQLException {
        //given
        String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_FULL);
        //when
        List<ServicePointVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5);
    }


    protected List<ServicePointVersion> executeQuery(String sqlQuery) throws SQLException {
        List<ServicePointVersion> result = new ArrayList<>();
        Connection connection = servicePointDataSource.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            assertThat(resultSet).isNotNull();
            while (resultSet.next()) {
                ServicePointVersionRowMapper servicePointVersionRowMapper = new ServicePointVersionRowMapper();
                ServicePointVersion servicePointVersion = servicePointVersionRowMapper.mapRow(resultSet, resultSet.getRow());
                result.add(servicePointVersion);
            }
        }
        connection.close();
        return result;
    }


    protected void insertSharedBusinessOrganisation(String sboid,String abbreviationIt, LocalDate validFrom, LocalDate validTo) throws SQLException {

        String insertSql = "insert into shared_business_organisation_version (id, sboid, abbreviation_de, abbreviation_fr," +
                " abbreviation_it, abbreviation_en, description_de," +
                " description_fr, description_it, description_en," +
                " organisation_number, status, valid_from, valid_to)" +
                " values (nextval('service_point_version_seq'), '"+sboid+"', 'TSDA', 'TSDA', '"+abbreviationIt+"', 'TSDA', 'Télésiège Les Dappes - La Dôle'," +
                " 'Télésiège Les Dappes - La Dôle', 'Télésiège Les Dappes - La Dôle', 'Télésiège Les Dappes - La Dôle', 3065, 'VALIDATED'," +
                " '"+formatDate(validFrom)+"', '"+formatDate(validTo)+"');";
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();;
    }

    protected void deleteSharedBusinessOrganisation(String sboid) throws SQLException {

        String deleteSql = "delete from service_point_version where sloid = '" + sboid + "'";
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
    }


}
