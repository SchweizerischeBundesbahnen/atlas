package ch.sbb.exportservice.reader;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.entity.geolocation.TrafficPointElementGeolocation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrafficPointElementVersionRowMapper extends BaseRowMapper implements RowMapper<TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("service_point_number")));
    TrafficPointElementVersion.TrafficPointElementVersionBuilder<?, ?> builder = TrafficPointElementVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.designation(rs.getString("designation"));
    builder.designationOperational(rs.getString("designation_operational"));
    builder.length(rs.getObject("length") != null ? rs.getDouble("length") : null);
    builder.trafficPointElementType(TrafficPointElementType.valueOf(rs.getString("traffic_point_element_type")));
    builder.boardingAreaHeight(rs.getObject("boarding_area_height") != null ? rs.getDouble("boarding_area_height") : null);
    builder.compassDirection(rs.getObject("compass_direction") != null ? rs.getDouble("compass_direction") : null);
    builder.parentSloid(rs.getString("parent_sloid"));
    getTrafficPointElementGeolocation(rs, builder);
    builder.parentSloidServicePoint(rs.getString("parent_service_point_sloid"));
    builder.servicePointBusinessOrganisation(getBusinessOrganisation(rs));
    builder.servicePointDesignationOfficial(rs.getString("designation_official"));
    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    builder.creator(rs.getString("creator"));
    builder.editor(rs.getString("editor"));
    builder.validFrom(rs.getObject("valid_from", LocalDate.class));
    builder.validTo(rs.getObject("valid_to", LocalDate.class));
    builder.version(rs.getInt("version"));
    TrafficPointElementVersion trafficPointElementVersion = builder.build();
    trafficPointElementVersion.setServicePointNumber(servicePointNumber);

    return trafficPointElementVersion;
  }

  private void getTrafficPointElementGeolocation(ResultSet rs,
      TrafficPointElementVersion.TrafficPointElementVersionBuilder<?, ?> builder) throws SQLException {
    TrafficPointElementGeolocation.TrafficPointElementGeolocationBuilder<?, ?> trafficPointElementGeolocationBuilder =
        TrafficPointElementGeolocation.builder();
    trafficPointElementGeolocationBuilder.east(rs.getDouble("east"));
    trafficPointElementGeolocationBuilder.north(rs.getDouble("north"));
    trafficPointElementGeolocationBuilder.height(RowMapperUtil.getDouble(rs, "height"));
    if (rs.getString("spatial_reference") != null) {
      SpatialReference spatialReference = SpatialReference.valueOf(rs.getString("spatial_reference"));
      trafficPointElementGeolocationBuilder.spatialReference(spatialReference);
    }
    TrafficPointElementGeolocation trafficPointElementGeolocation = trafficPointElementGeolocationBuilder.build();
    if (trafficPointElementGeolocation.getSpatialReference() != null) {
      builder.trafficPointElementGeolocation(trafficPointElementGeolocation);
    }
  }

}