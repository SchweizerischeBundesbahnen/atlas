package ch.sbb.exportservice.reader;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.LoadingPointVersion;
import ch.sbb.exportservice.entity.LoadingPointVersion.LoadingPointVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LoadingPointVersionRowMapper extends BaseRowMapper implements RowMapper<LoadingPointVersion> {

  @Override
  public LoadingPointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ServicePointNumber servicePointNumber = ServicePointNumber.of(rs.getInt("service_point_number"));
    LoadingPointVersionBuilder<?, ?> builder = LoadingPointVersion.builder();
    builder.id(rs.getLong("id"));
    builder.number(rs.getInt("number"));
    builder.designation(rs.getString("designation"));
    builder.designationLong(rs.getString("designation_long"));
    builder.connectionPoint(rs.getBoolean("connection_point"));
    builder.servicePointNumber(servicePointNumber);
    builder.servicePointBusinessOrganisation(getBusinessOrganisation(rs));
    builder.parentSloidServicePoint(rs.getString("parent_service_point_sloid"));
    builder.validFrom(rs.getObject("valid_from", LocalDate.class));
    builder.validTo(rs.getObject("valid_to", LocalDate.class));
    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    builder.creator(rs.getString("creator"));
    builder.editor(rs.getString("editor"));
    builder.version(rs.getInt("version"));
    return builder.build();
  }

}
