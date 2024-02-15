package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ParkingLotVersion;
import ch.sbb.exportservice.entity.ParkingLotVersion.ParkingLotVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class ParkingLotVersionRowMapper extends BaseRowMapper implements RowMapper<ParkingLotVersion> {

  @Override
  public ParkingLotVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    ParkingLotVersionBuilder<?, ?> builder = ParkingLotVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
    builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.designation(rs.getString("designation"));
    builder.additionalInformation(rs.getString("additional_information"));
    builder.placesAvailable(BooleanOptionalAttributeType.valueOf(rs.getString("places_available")));
    builder.prmPlacesAvailable(BooleanOptionalAttributeType.valueOf(rs.getString("prm_places_available")));
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
