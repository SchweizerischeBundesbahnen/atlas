package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.lidi.Subline;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SublineRowMapper implements RowMapper<Subline> {

  @Override
  public Subline mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Subline.builder()
        .slnid(rs.getString("slnid"))
        .mainlineSlnid(rs.getString("mainline_slnid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .status(Status.valueOf(rs.getString("status")))
        .sublineType(SublineType.valueOf(rs.getString("subline_type")))
        .concessionType(SublineConcessionType.valueOf(rs.getString("concession_type")))
        .swissSublineNumber(rs.getString("swiss_subline_number"))
        .description(rs.getString("description"))
        .longName(rs.getString("long_name"))
        .businessOrganisation(rs.getString("business_organisation"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .build();
  }

}
