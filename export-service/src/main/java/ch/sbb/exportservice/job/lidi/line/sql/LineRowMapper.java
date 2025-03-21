package ch.sbb.exportservice.job.lidi.line.sql;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;

public class LineRowMapper implements RowMapper<Line> {

  @Override
  public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Line.builder()
        .id(rs.getLong("id"))
        .slnid(rs.getString("slnid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .status(Status.valueOf(rs.getString("status")))
        .lineType(LineType.valueOf(rs.getString("line_type")))
        .concessionType(Optional.ofNullable(rs.getString("concession_type")).map(LineConcessionType::valueOf).orElse(null))
        .swissLineNumber(rs.getString("swiss_line_number"))
        .description(rs.getString("description"))
        .longName(rs.getString("long_name"))
        .number(rs.getString("number"))
        .shortNumber(rs.getString("short_number"))
        .offerCategory(Optional.ofNullable(rs.getString("offer_category")).map(OfferCategory::valueOf).orElse(null))
        .businessOrganisation(rs.getString("business_organisation"))
        .comment(rs.getString("comment"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .build();
  }

}
