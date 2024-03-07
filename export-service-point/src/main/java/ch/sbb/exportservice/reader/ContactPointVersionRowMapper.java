package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.prm.ContactPointVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class ContactPointVersionRowMapper extends BaseRowMapper implements RowMapper<ContactPointVersion> {

    @Override
    public ContactPointVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContactPointVersion.ContactPointVersionBuilder<?, ?> builder = ContactPointVersion.builder();
        builder.id(rs.getLong("id"));
        builder.sloid(rs.getString("sloid"));
        builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
        builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
        builder.type(ContactPointType.valueOf(rs.getString("type")));
        builder.designation(rs.getString("designation"));
        builder.additionalInformation(rs.getString("additional_information"));
        builder.inductionLoop(StandardAttributeType.valueOf(rs.getString("induction_loop")));
        builder.openingHours(rs.getString("opening_hours"));
        builder.wheelchairAccess(StandardAttributeType.valueOf(rs.getString("wheelchair_access")));
        builder.validFrom(rs.getObject("valid_from", LocalDate.class));
        builder.validTo(rs.getObject("valid_to", LocalDate.class));
        builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
        builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
        builder.creator(rs.getString("creator"));
        builder.editor(rs.getString("editor"));
        builder.version(rs.getInt("version"));
        builder.status(Status.valueOf(rs.getString("status")));
        return builder.build();
    }

}
