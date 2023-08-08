package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.reader.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.reader.TrafficPointElementVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TrafficPointElementVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest{

    @Test
    public void shouldReturnWorldFullWithServicePointAndBusinessOrganisation() throws SQLException {
        //given
        int servicePointNumber = 12058870;
        insertServicePoint(servicePointNumber, LocalDate.of(2020,1,1),LocalDate.of(2099,12,31), Country.SWITZERLAND);
        String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_FULL);
        //when
        List<TrafficPointElementVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5);
        TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> t.getServicePointNumber().getValue() == 12058870).findFirst().get();
        assertThat(trafficPointElementVersion).isNotNull();
        assertThat(trafficPointElementVersion.getServicePointBusinessOrganisation()).isNotNull();

        deleteServicePoint(servicePointNumber);
    }

    @Test
    public void shouldReturnTimetableFuture() throws SQLException {
        //given
        LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
        int servicePointNumber = 12058870;
        insertServicePoint(servicePointNumber, LocalDate.of(2020,1,1),LocalDate.of(2099,12,31), Country.SWITZERLAND);
        String sloid = "ch:1:sloid:77559:0:2";
        insertTrafficPoint(sloid,now,now);
        String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);
        //when
        List<TrafficPointElementVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> t.getSloid().equals(sloid)).findFirst().get();
        assertThat(trafficPointElementVersion).isNotNull();
        result.forEach(t -> assertThat(isDateInRange(now,t.getValidFrom(),t.getValidTo())).isTrue());
        deleteServicePoint(servicePointNumber);
        deleteTrafficPoint(sloid);
    }

    @Test
    public void shouldReturnWorldActual() throws SQLException {
        //given
        int servicePointNumber = 12058870;
        insertServicePoint(servicePointNumber, LocalDate.of(2020,1,1),LocalDate.of(2099,12,31), Country.SWITZERLAND);
        String sloid = "ch:1:sloid:77559:0:2";
        LocalDate now = LocalDate.now();
        insertTrafficPoint(sloid, now, now);
        String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);
        //when
        List<TrafficPointElementVersion> result = executeQuery(sqlQuery);
        //then
        assertThat(result).isNotEmpty();
        TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> t.getSloid().equals(sloid)).findFirst().get();
        assertThat(trafficPointElementVersion).isNotNull();
        result.forEach(t -> assertThat(isDateInRange(now,t.getValidFrom(),t.getValidTo())).isTrue());

        deleteServicePoint(servicePointNumber);
        deleteTrafficPoint(sloid);
    }

    protected List<TrafficPointElementVersion> executeQuery(String sqlQuery) throws SQLException {
        List<TrafficPointElementVersion> result = new ArrayList<>();
        Connection connection = servicePointDataSource.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            assertThat(resultSet).isNotNull();
            while (resultSet.next()) {
                TrafficPointElementVersionRowMapper mapper = new TrafficPointElementVersionRowMapper();
                TrafficPointElementVersion trafficPointElementVersion = mapper.mapRow(resultSet, resultSet.getRow());
                result.add(trafficPointElementVersion);
            }
        }
        connection.close();
        return result;
    }


    boolean isDateInRange(LocalDate matchDate, LocalDate validFrom, LocalDate validTo){
        if((matchDate.equals(validFrom) || matchDate.isAfter(validFrom))
                && (matchDate.equals(validTo) || matchDate.isBefore(validTo))){
                return true;
        }
        return false;
    }

//    protected void insertServicePoint(Integer number, Country country) throws SQLException {
//
//        String insertSql = "insert into service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country, designation_long," +
//                " designation_official, abbreviation, status_didok3, sort_code_of_destination_station," +
//                " business_organisation, operating_point_type, stop_point_type, status," +
//                " operating_point_kilometer_master, operating_point_route_network, valid_from, valid_to," +
//                " creation_date, creator, edition_date, editor, version, freight_service_point, operating_point," +
//                " operating_point_with_timetable, operating_point_technical_timetable_type," +
//                " operating_point_traffic_point_type)" +
//                " values (nextval('service_point_version_seq'), 1002, " + number + ", null, 5887, '" + country.name() + "', null, 'Trins, Waldfestplatz', null, 'IN_OPERATION', null," +
//                " 'ch:1:sboid:101999', null, 'UNKNOWN', 'VALIDATED', null, false, '" + formatDate(LocalDate.of(2020,1,1)) + "', '" + formatDate(LocalDate.of(2099,12,31)) + "'," +
//                " '2022-09-10 17:29:29.000000', 'fs45117', '2022-09-10 17:29:29.000000', 'fs45117', 0, false, true, true, null, null);";
//
//        Connection connection = servicePointDataSource.getConnection();
//        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
//            preparedStatement.executeUpdate();
//        }
//        connection.close();
//    }

    protected void insertTrafficPoint(String sloid, LocalDate validFrom, LocalDate validTo) throws SQLException {

        String insertSql = "insert into traffic_point_element_version (id, sloid, parent_sloid, designation," +
                " designation_operational, traffic_point_element_type, length, boarding_area_height, compass_direction," +
                " service_point_number, valid_from, valid_to, traffic_point_geolocation_id, creation_date, creator," +
                " edition_date, editor, version)" +
                "values (nextval('traffic_point_element_version_seq'), '" + sloid + "', null, null, '2', '0', 18.000, 2.00, null, 12058870, " +
                "'"+ formatDate(validFrom)+ "', '" + formatDate(validTo)+"',1000, '2022-03-03 07:56:42.000000', 'fs45117', '2022-05-03 11:50:46.000000', 'e536178', 0);";

        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

    protected void deleteTrafficPoint(String sloid) throws SQLException {

        String deleteSql = "delete from traffic_point_element_version where sloid ='" + sloid + "'";
        Connection connection = servicePointDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

}
