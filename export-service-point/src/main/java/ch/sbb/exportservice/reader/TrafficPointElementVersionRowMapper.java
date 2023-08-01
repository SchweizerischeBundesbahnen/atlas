package ch.sbb.exportservice.reader;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.exportservice.entity.BusinessOrganisation;
import ch.sbb.exportservice.entity.TrafficPointElementVersion;
import ch.sbb.exportservice.entity.geolocation.TrafficPointElementGeolocation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrafficPointElementVersionRowMapper implements RowMapper<TrafficPointElementVersion> {

  @Override
  public TrafficPointElementVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ServicePointNumber servicePointNumber = ServicePointNumber.of(rs.getInt("service_point_number"));
    TrafficPointElementVersion.TrafficPointElementVersionBuilder<?, ?> builder = TrafficPointElementVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.designation(rs.getString("designation"));
    builder.designationOperational(rs.getString("designation_operational"));
    builder.length(rs.getDouble("length"));
    builder.trafficPointElementType(TrafficPointElementType.fromValue(rs.getInt("traffic_point_element_type")));
    builder.boardingAreaHeight(rs.getDouble("boarding_area_height"));
    builder.compassDirection(rs.getDouble("compass_direction"));
    builder.parentSloid(rs.getString("parent_sloid"));
    builder.servicePointNumber(ServicePointNumber.of(rs.getInt("number")));
    getTrafficPointElementGeolocation(rs,builder);
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

  private BusinessOrganisation getBusinessOrganisation(ResultSet rs) throws SQLException {
    return BusinessOrganisation.builder()
        .businessOrganisation(rs.getString("business_organisation"))
        .businessOrganisationNumber(RowMapperUtil.getInteger(rs, "organisation_number"))
        .businessOrganisationAbbreviationDe(rs.getString("abbreviation_de"))
        .businessOrganisationAbbreviationFr(rs.getString("abbreviation_fr"))
        .businessOrganisationAbbreviationEn(rs.getString("abbreviation_en"))
        .businessOrganisationAbbreviationIt(rs.getString("abbreviation_it"))
        .businessOrganisationDescriptionDe(rs.getString("description_de"))
        .businessOrganisationDescriptionFr(rs.getString("description_fr"))
        .businessOrganisationDescriptionEn(rs.getString("description_en"))
        .businessOrganisationDescriptionIt(rs.getString("description_it")).build();
  }

  private void getTrafficPointElementGeolocation(ResultSet rs,  TrafficPointElementVersion.TrafficPointElementVersionBuilder<?, ?> builder) throws SQLException {
    TrafficPointElementGeolocation.TrafficPointElementGeolocationBuilder<?, ?> trafficPointElementGeolocationBuilder = TrafficPointElementGeolocation.builder();
    trafficPointElementGeolocationBuilder.east(rs.getDouble("east"));
    trafficPointElementGeolocationBuilder.north(rs.getDouble("north"));
    trafficPointElementGeolocationBuilder.height(RowMapperUtil.getDouble(rs, "height"));
    if (rs.getString("spatial_reference") != null) {
      SpatialReference spatialReference = SpatialReference.valueOf(rs.getString("spatial_reference"));
      trafficPointElementGeolocationBuilder.spatialReference(spatialReference);
    }
    TrafficPointElementGeolocation trafficPointElementGeolocation = trafficPointElementGeolocationBuilder.build();
    if(trafficPointElementGeolocation.getSpatialReference() != null){
      builder.trafficPointElementGeolocation(trafficPointElementGeolocation);
    }

  }

}