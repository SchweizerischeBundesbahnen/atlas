package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ToiletVersion;
import ch.sbb.exportservice.entity.ToiletVersion.ToiletVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class ToiletVersionRowMapper extends BaseRowMapper implements RowMapper<ToiletVersion> {

  @Override
  public ToiletVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ToiletVersionBuilder<?, ?> builder = ToiletVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
    builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.designation(rs.getString("designation"));
    builder.additionalInformation(rs.getString("additional_information"));
    builder.wheelchairToilet(
        rs.getObject("wheelchair_toilet") != null ?
            StandardAttributeType.valueOf(rs.getString("wheelchair_toilet")) : null);
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
