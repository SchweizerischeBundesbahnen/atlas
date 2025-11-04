package ch.sbb.exportservice.job.lidi.ttfn.sql;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.ttfn.entity.TimetableFieldNumber;
import ch.sbb.exportservice.util.RowMapperUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;
import org.springframework.jdbc.core.RowMapper;

public class TimetableFieldNumberRowMapper implements RowMapper<TimetableFieldNumber> {

  @Override
  public TimetableFieldNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
    return TimetableFieldNumber.builder()
        .id(rs.getLong("id"))
        .ttfnid(rs.getString("ttfnid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .status(Status.valueOf(rs.getString("status")))
        .swissTimetableFieldNumber(rs.getString("swiss_timetable_field_number"))
        .number(rs.getString("number"))
        .businessOrganisation(rs.getString("business_organisation"))
        .descriptionOutwardLine1(rs.getString("description_outward_line_1"))
        .descriptionOutwardLine2(rs.getString("description_outward_line_2"))
        .descriptionOutwardLine3(rs.getString("description_outward_line_3"))
        .descriptionReturnLine1(rs.getString("description_return_line_1"))
        .descriptionReturnLine2(rs.getString("description_return_line_2"))
        .descriptionReturnLine3(rs.getString("description_return_line_3"))
        .meanOfTransport(RowMapperUtil.enumValueElseNull(TtfnMeanOfTransport.class, rs.getString("mean_of_transport")))
        .lineRelations(RowMapperUtil.stringToSet(rs.getString("slnids"), Function.identity()))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .build();
  }

}
