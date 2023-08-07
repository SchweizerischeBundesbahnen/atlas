package ch.sbb.exportservice.integration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseSqlIntegrationTest {

    @Autowired
    @Qualifier("servicePointDataSource")
    protected DataSource servicePointDataSource;

    protected void deleteServicePoint(Integer number) throws SQLException {

        String deleteSql = "delete from service_point_version where number =" + number;
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
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

    protected String formatDate(LocalDate localDate) {
        return localDate.format(
                DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    }

}
