package ch.sbb.exportservice.reader;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.ContactPointVersion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContactPointVersionRowMapper extends BaseRowMapper implements RowMapper<ContactPointVersion> {

    @Override
    public ContactPointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContactPointVersion.ContactPointVersionBuilder<?, ?> builder = ContactPointVersion.builder();
        builder.id(rs.getLong("id"));
        builder.sloid(rs.getString("sloid"));
        builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
        builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
        builder.type(rs.getString("type"));
        builder.designation(rs.getString("designation"));
        builder.additionalInformation(rs.getString("additional_information"));
        builder.inductionLoop(rs.getString("induction_loop"));
        builder.openingHours(rs.getString("opening_hours"));
        builder.wheelchairAccess(rs.getString("wheelchair_access"));
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
