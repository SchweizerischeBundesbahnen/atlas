package ch.sbb.exportservice.reader;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation.ServicePointGeolocationBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;

public class ServicePointVersionRowMapper extends BaseRowMapper implements RowMapper<ServicePointVersion> {

  @Override
  public ServicePointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ServicePointVersionBuilder<?, ?> servicePointVersionBuilder = ServicePointVersion.builder();
    servicePointVersionBuilder.id(rs.getLong("id"));
    servicePointVersionBuilder.number(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
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

    setMeansOfTransport(servicePointVersionBuilder, rs.getString("list_of_transports"));
    setCategories(servicePointVersionBuilder, rs.getString("list_of_categories"));

    if (rs.getString("operating_point_traffic_point_type") != null) {
      servicePointVersionBuilder.operatingPointTrafficPointType(OperatingPointTrafficPointType.valueOf(rs.getString(
          "operating_point_traffic_point_type")));
    }
    servicePointVersionBuilder.operatingPointRouteNetwork(rs.getBoolean("operating_point_route_network"));
    Integer operatingPointKilometerMaster = RowMapperUtil.getInteger(rs, "operating_point_kilometer_master");
    if (operatingPointKilometerMaster != null) {
      servicePointVersionBuilder.operatingPointKilometerMaster(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(operatingPointKilometerMaster)));
    }
    servicePointVersionBuilder.sortCodeOfDestinationStation(rs.getString("sort_code_of_destination_station"));

    servicePointVersionBuilder.businessOrganisation(getBusinessOrganisation(rs));

    Optional.ofNullable(rs.getString("status"))
        .ifPresent(status -> servicePointVersionBuilder.status(Status.valueOf(status)));

    servicePointVersionBuilder.comment(rs.getString("fot_comment"));
    servicePointVersionBuilder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    servicePointVersionBuilder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    servicePointVersionBuilder.creator(rs.getString("creator"));
    servicePointVersionBuilder.editor(rs.getString("editor"));
    servicePointVersionBuilder.version(rs.getInt("version"));
    return servicePointVersionBuilder.build();
  }

  private void getServicePointGeolocation(ResultSet rs, ServicePointVersionBuilder<?, ?> servicePointVersionBuilder)
      throws SQLException {
    ServicePointGeolocationBuilder<?, ?> servicePointGeolocationBuilder = ServicePointGeolocation.builder();
    servicePointGeolocationBuilder.east(rs.getDouble("east"));
    servicePointGeolocationBuilder.north(rs.getDouble("north"));
    servicePointGeolocationBuilder.height(RowMapperUtil.getDouble(rs, "height"));

    if (rs.getString("spatial_reference") != null) {
      SpatialReference spatialReference = SpatialReference.valueOf(rs.getString("spatial_reference"));
      servicePointGeolocationBuilder.spatialReference(spatialReference);
    }

    String geolocationCountry = rs.getString("geolocation_country");
    if (geolocationCountry != null) {
      servicePointGeolocationBuilder.country(Country.valueOf(geolocationCountry));
    }
    if (rs.getString("swiss_canton") != null) {
      servicePointGeolocationBuilder.swissCanton(SwissCanton.valueOf(rs.getString("swiss_canton")));
    }

    servicePointGeolocationBuilder.swissDistrictName(rs.getString("swiss_district_name"));
    servicePointGeolocationBuilder.swissDistrictNumber(RowMapperUtil.getInteger(rs, "swiss_district_number"));
    servicePointGeolocationBuilder.swissMunicipalityNumber(RowMapperUtil.getInteger(rs, "swiss_municipality_number"));
    servicePointGeolocationBuilder.swissMunicipalityName(rs.getString("swiss_municipality_name"));
    servicePointGeolocationBuilder.swissLocalityName(rs.getString("swiss_locality_name"));
    ServicePointGeolocation servicePointGeolocation = servicePointGeolocationBuilder.build();
    if (servicePointGeolocation.getSpatialReference() != null) {
      servicePointVersionBuilder.servicePointGeolocation(servicePointGeolocation);
    }
  }

  void setCategories(ServicePointVersionBuilder<?, ?> servicePointVersionBuilder, String listOfCategories) {
    if (listOfCategories != null) {
      Set<Category> categories = RowMapperUtil.stringToSet(listOfCategories, Category::valueOf);

      servicePointVersionBuilder.categories(categories);
      servicePointVersionBuilder.categoriesPipeList(RowMapperUtil.toPipedString(categories));
    }
  }

  void setMeansOfTransport(ServicePointVersionBuilder<?, ?> servicePointVersionBuilder, String listOfMeansOfTransport) {
    if (listOfMeansOfTransport != null) {
      Set<MeanOfTransport> meansOfTransport = RowMapperUtil.stringToSet(listOfMeansOfTransport, MeanOfTransport::valueOf);

      servicePointVersionBuilder.meansOfTransport(meansOfTransport);
      servicePointVersionBuilder.meansOfTransportPipeList(RowMapperUtil.toPipedString(meansOfTransport));
    }
  }



}
