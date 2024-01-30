package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import ch.sbb.exportservice.entity.ReferencePointVersion.ReferencePointVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class ReferencePointVersionRowMapper extends BaseRowMapper implements RowMapper<ReferencePointVersion> {

  @Override
  public ReferencePointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ReferencePointVersionBuilder<?, ?> builder = ReferencePointVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
    builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.designation(rs.getString("designation"));
    builder.designation(rs.getString("additional_information"));
    builder.mainReferencePoint(rs.getBoolean("main_reference_point"));
    builder.referencePointType(ReferencePointAttributeType.valueOf(rs.getString("reference_point_type")));
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
