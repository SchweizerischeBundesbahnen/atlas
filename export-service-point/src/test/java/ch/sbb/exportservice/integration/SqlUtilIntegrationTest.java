package ch.sbb.exportservice.integration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.reader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.SqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class SqlUtilIntegrationTest {

    @Autowired
    @Qualifier("servicePointDataSource")
    protected DataSource servicePointDataSource;

    @Test
    public void shouldReturnWorldOnlyActualWithActualBusinessOrganisationData() throws SQLException {
        //given
        LocalDate now = LocalDate.now();
        int servicePointNumber = 19058867;
        insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
        String sboid = "ch:1:sboid:101999";
        insertSharedBusinessOrganisation(sboid,"abb",now,now);
        insertSharedBusinessOrganisation(sboid,"abbIt",now.plusMonths(1),now.plusMonths(2));
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.WORLD_ONLY_ACTUAL);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.WORLD_ONLY_ACTUAL);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.WORLD_FULL);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.WORLD_ONLY_ACTUAL);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.WORLD_ONLY_TIMETABLE_FUTURE);
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

        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.SWISS_ONLY_TIMETABLE_FUTURE);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.SWISS_ONLY_ACTUAL);
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
        String sqlQuery = SqlQueryUtil.getSqlQuery(ServicePointExportType.SWISS_ONLY_FULL);
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

    protected void insertServicePoint(Integer number, LocalDate validFrom, LocalDate validTo, Country country) throws SQLException {

        String insertSql = "insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country, designation_long," +
                " designation_official, abbreviation, status_didok3, sort_code_of_destination_station," +
                " business_organisation, operating_point_type, stop_point_type, status," +
                " operating_point_kilometer_master, operating_point_route_network, valid_from, valid_to," +
                " creation_date, creator, edition_date, editor, version, freight_service_point, operating_point," +
                " operating_point_with_timetable, operating_point_technical_timetable_type," +
                " operating_point_traffic_point_type)" +
                " values (nextval('service_point_version_seq'), 1002, " + number + ", null, 5887, '" + country.name() + "', null, 'Trins, Waldfestplatz', null, 'IN_OPERATION', null," +
                " 'ch:1:sboid:101999', null, 'UNKNOWN', 'VALIDATED', null, false, '" + formatDate(validFrom) + "', '" + formatDate(validTo) + "'," +
                " '2022-09-10 17:29:29.000000', 'fs45117', '2022-09-10 17:29:29.000000', 'fs45117', 0, false, true, true, null, null);";
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
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

    protected void deleteServicePoint(Integer number) throws SQLException {

        String deleteSql = "delete from service_point_version where number =" + number;
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

    protected void deleteSharedBusinessOrganisation(String sboid) throws SQLException {

        String deleteSql = "delete from service_point_version where sloid = '" + sboid + "'";
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

    protected String formatDate(LocalDate localDate) {
        return localDate.format(
                DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    }
}
