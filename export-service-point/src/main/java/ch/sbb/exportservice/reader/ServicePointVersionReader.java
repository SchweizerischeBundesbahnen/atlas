package ch.sbb.exportservice.reader;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.enumeration.Category;
import ch.sbb.exportservice.entity.enumeration.Country;
import ch.sbb.exportservice.entity.enumeration.MeanOfTransport;
import ch.sbb.exportservice.entity.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.exportservice.entity.enumeration.OperatingPointTrafficPointType;
import ch.sbb.exportservice.entity.enumeration.OperatingPointType;
import ch.sbb.exportservice.entity.enumeration.ServicePointStatus;
import ch.sbb.exportservice.entity.enumeration.StopPointType;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import ch.sbb.exportservice.entity.model.ServicePointNumber;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ServicePointVersionReader extends JdbcCursorItemReader<ServicePointVersion> implements
    ItemReader<ServicePointVersion> {

  public ServicePointVersionReader(@Autowired
  @Qualifier("servicePointDataSource")
  DataSource dataSource) {
    setDataSource(dataSource);
    //    setSql("SELECT * FROM service_point_version spv "
    //        + "LEFT JOIN service_point_version_categories spvc on spv.id = spvc.service_point_version_id and spvc.categories
    //        is null "
    //        + "LEFT JOIN service_point_version_geolocation spvg on spv.service_point_geolocation_id = spvg.id "
    //        + "LEFT JOIN service_point_version_means_of_transport spvmot on spv.id = spvmot.service_point_version_id and spvmot"
    //        + ".means_of_transport is null"
    //    );
    setSql("SELECT spv.id, string_agg(spvmot.means_of_transport, '|') as list_of_transports, string_agg(spvc.categories, '|') "
        + "as list_of_categories, spv.*, spvg.* "
        + "FROM service_point_version spv "
        + "LEFT JOIN service_point_version_means_of_transport spvmot "
        + "on spv.id = spvmot.service_point_version_id "
        + "LEFT JOIN service_point_version_categories spvc on spv.id = spvc.service_point_version_id "
        + "LEFT JOIN service_point_version_geolocation spvg on spv.service_point_geolocation_id = spvg.id "
        + "group by spv.id,spvg.id"
    );
    setFetchSize(100);
    setRowMapper(new ServicePointVersionRowMapper());
  }

  public static class ServicePointVersionRowMapper implements RowMapper<ServicePointVersion> {

    @Override
    public ServicePointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
      ServicePointVersion servicePointVersion = new ServicePointVersion();
      servicePointVersion.setId(rs.getLong("id"));
      servicePointVersion.setNumber(ServicePointNumber.of(rs.getInt("number")));
      servicePointVersion.setCountry(Country.valueOf(rs.getString("country")));
      servicePointVersion.setSloid(rs.getString("sloid"));
      servicePointVersion.setValidFrom(rs.getObject("valid_from", LocalDate.class));
      servicePointVersion.setValidTo(rs.getObject("valid_to", LocalDate.class));
      servicePointVersion.setDesignationOfficial(rs.getString("designation_official"));
      servicePointVersion.setDesignationLong(rs.getString("designation_long"));
      servicePointVersion.setAbbreviation(rs.getString("abbreviation"));
      servicePointVersion.setOperatingPoint(rs.getBoolean("operating_point"));
      servicePointVersion.setOperatingPointWithTimetable(rs.getBoolean("operating_point_with_timetable"));
      if (rs.getString("stop_point_type") != null) {
        servicePointVersion.setStopPointType(StopPointType.valueOf(rs.getString("stop_point_type")));
      }
      servicePointVersion.setFreightServicePoint(rs.getBoolean("freight_service_point"));

      ServicePointGeolocation servicePointGeolocation = new ServicePointGeolocation();
      servicePointGeolocation.setCountry(Country.valueOf(rs.getString("country")));
      if (rs.getString("swiss_canton") != null) {
        servicePointGeolocation.setSwissCanton(SwissCanton.valueOf(rs.getString("swiss_canton")));
      }
      servicePointGeolocation.setSwissDistrictName(rs.getString("swiss_district_name"));
      servicePointGeolocation.setSwissDistrictNumber(rs.getInt("swiss_district_number"));
      servicePointGeolocation.setSwissMunicipalityNumber(rs.getInt("swiss_municipality_number"));
      servicePointGeolocation.setSwissMunicipalityName(rs.getString("swiss_municipality_name"));
      servicePointGeolocation.setSwissLocalityName(rs.getString("swiss_locality_name"));
      servicePointVersion.setServicePointGeolocation(servicePointGeolocation);
      if (rs.getString("operating_point_type") != null) {
        servicePointVersion.setOperatingPointType(OperatingPointType.valueOf(rs.getString("operating_point_type")));
      }
      if (rs.getString("operating_point_technical_timetable_type") != null) {
        servicePointVersion.setOperatingPointTechnicalTimetableType(
            OperatingPointTechnicalTimetableType.valueOf(rs.getString("operating_point_technical_timetable_type")));
      }
      if (rs.getString("list_of_transports") != null) {
        String listOfMeansOfTransports = rs.getString("list_of_transports");
        Set<MeanOfTransport> collectedMeansOfTransport = Arrays.stream(listOfMeansOfTransports.split("\\|")).map(
            MeanOfTransport::valueOf).collect(Collectors.toSet());
        servicePointVersion.setMeansOfTransportPipeList(rs.getString("list_of_transports"));
        servicePointVersion.setMeansOfTransport(collectedMeansOfTransport);
      }
      if (rs.getString("list_of_categories") != null) {
        String listOfCategories = rs.getString("list_of_categories");
        servicePointVersion.setCategoriesPipeList(listOfCategories);
        Set<Category> collectedCategories = Arrays.stream(listOfCategories.split("\\|")).map(Category::valueOf)
            .collect(Collectors.toSet());
        servicePointVersion.setCategories(collectedCategories);
      }
      if (rs.getString("operating_point_traffic_point_type") != null) {
        servicePointVersion.setOperatingPointTrafficPointType(OperatingPointTrafficPointType.valueOf(rs.getString(
            "operating_point_traffic_point_type")));
      }
      servicePointVersion.setOperatingPointRouteNetwork(rs.getBoolean("operating_point_route_network"));
      servicePointVersion.setOperatingPointKilometerMaster(ServicePointNumber.of(rs.getInt("operating_point_kilometer_master")));
      servicePointVersion.setSortCodeOfDestinationStation(rs.getString("sort_code_of_destination_station"));
      servicePointVersion.setBusinessOrganisation(rs.getString("business_organisation"));
      servicePointVersion.setComment(rs.getString("comment"));
      servicePointVersion.setCreationDate(rs.getObject("creation_date", LocalDateTime.class));
      servicePointVersion.setEditionDate(rs.getObject("edition_date", LocalDateTime.class));
      servicePointVersion.setStatusDidok3(ServicePointStatus.valueOf(rs.getString("status_didok3")));
      return servicePointVersion;
    }
  }
}
