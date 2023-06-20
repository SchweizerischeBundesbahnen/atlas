package ch.sbb.exportservice.reader;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation.ServicePointGeolocationBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.RowMapper;

public class ServicePointVersionRowMapper implements RowMapper<ServicePointVersion> {

  @Override
  public ServicePointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ServicePointVersionBuilder<?, ?> servicePointVersionBuilder = ServicePointVersion.builder();
    servicePointVersionBuilder.id(rs.getLong("id"));
    servicePointVersionBuilder.number(ServicePointNumber.of(rs.getInt("number")));
    servicePointVersionBuilder.country(Country.valueOf(rs.getString("country")));
    servicePointVersionBuilder.sloid(rs.getString("sloid"));
    servicePointVersionBuilder.validFrom(rs.getObject("valid_from", LocalDate.class));
    servicePointVersionBuilder.validTo(rs.getObject("valid_to", LocalDate.class));
    servicePointVersionBuilder.designationOfficial(rs.getString("designation_official"));
    servicePointVersionBuilder.designationLong(rs.getString("designation_long"));
    servicePointVersionBuilder.abbreviation(rs.getString("abbreviation"));
    servicePointVersionBuilder.operatingPoint(rs.getBoolean("operating_point"));
    servicePointVersionBuilder.operatingPointWithTimetable(rs.getBoolean("operating_point_with_timetable"));
    if (rs.getString("stop_point_type") != null) {
      servicePointVersionBuilder.stopPointType(StopPointType.valueOf(rs.getString("stop_point_type")));
    }
    servicePointVersionBuilder.freightServicePoint(rs.getBoolean("freight_service_point"));
    getServicePointGeolocation(rs, servicePointVersionBuilder);
    if (rs.getString("operating_point_type") != null) {
      servicePointVersionBuilder.operatingPointType(OperatingPointType.valueOf(rs.getString("operating_point_type")));
    }
    if (rs.getString("operating_point_technical_timetable_type") != null) {
      servicePointVersionBuilder.operatingPointTechnicalTimetableType(
          OperatingPointTechnicalTimetableType.valueOf(rs.getString("operating_point_technical_timetable_type")));
    }
    if (rs.getString("list_of_transports") != null) {
      String listOfMeansOfTransports = rs.getString("list_of_transports");
      Set<MeanOfTransport> collectedMeansOfTransport = Arrays.stream(listOfMeansOfTransports.split("\\|")).map(
          MeanOfTransport::valueOf).collect(Collectors.toSet());
      servicePointVersionBuilder.meansOfTransportPipeList(rs.getString("list_of_transports"));
      servicePointVersionBuilder.meansOfTransport(collectedMeansOfTransport);
    }
    if (rs.getString("list_of_categories") != null) {
      String listOfCategories = rs.getString("list_of_categories");
      servicePointVersionBuilder.categoriesPipeList(listOfCategories);
      Set<Category> collectedCategories = Arrays.stream(listOfCategories.split("\\|")).map(Category::valueOf)
          .collect(Collectors.toSet());
      servicePointVersionBuilder.categories(collectedCategories);
    }
    if (rs.getString("operating_point_traffic_point_type") != null) {
      servicePointVersionBuilder.operatingPointTrafficPointType(OperatingPointTrafficPointType.valueOf(rs.getString(
          "operating_point_traffic_point_type")));
    }
    servicePointVersionBuilder.operatingPointRouteNetwork(rs.getBoolean("operating_point_route_network"));
    servicePointVersionBuilder.operatingPointKilometerMaster(
        ServicePointNumber.of(rs.getInt("operating_point_kilometer_master")));
    servicePointVersionBuilder.sortCodeOfDestinationStation(rs.getString("sort_code_of_destination_station"));
    servicePointVersionBuilder.businessOrganisation(rs.getString("business_organisation"));
    servicePointVersionBuilder.comment(rs.getString("comment"));
    servicePointVersionBuilder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    servicePointVersionBuilder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    servicePointVersionBuilder.statusDidok3(ServicePointStatus.valueOf(rs.getString("status_didok3")));
    servicePointVersionBuilder.creator(rs.getString("creator"));
    servicePointVersionBuilder.editor(rs.getString("editor"));
    return servicePointVersionBuilder.build();
  }

  private void getServicePointGeolocation(ResultSet rs, ServicePointVersionBuilder<?, ?> servicePointVersionBuilder)
      throws SQLException {
    ServicePointGeolocationBuilder<?, ?> servicePointGeolocationBuilder = ServicePointGeolocation.builder();
    servicePointGeolocationBuilder.east(rs.getDouble("east"));
    servicePointGeolocationBuilder.north(rs.getDouble("north"));
    servicePointGeolocationBuilder.height(rs.getDouble("height"));

    if (rs.getString("spatial_reference") != null) {
      SpatialReference spatialReference = SpatialReference.valueOf(rs.getString("spatial_reference"));
      servicePointGeolocationBuilder.spatialReference(spatialReference);
    }

    servicePointGeolocationBuilder.country(Country.valueOf(rs.getString("country")));
    if (rs.getString("swiss_canton") != null) {
      servicePointGeolocationBuilder.swissCanton(SwissCanton.valueOf(rs.getString("swiss_canton")));
    }
    
    servicePointGeolocationBuilder.swissDistrictName(rs.getString("swiss_district_name"));
    servicePointGeolocationBuilder.swissDistrictNumber(rs.getInt("swiss_district_number"));
    servicePointGeolocationBuilder.swissMunicipalityNumber(rs.getInt("swiss_municipality_number"));
    servicePointGeolocationBuilder.swissMunicipalityName(rs.getString("swiss_municipality_name"));
    servicePointGeolocationBuilder.swissLocalityName(rs.getString("swiss_locality_name"));
    ServicePointGeolocation servicePointGeolocation = servicePointGeolocationBuilder.build();
    if (servicePointGeolocation.getSpatialReference() != null) {
      servicePointVersionBuilder.servicePointGeolocation(servicePointGeolocation);
    }
  }
}