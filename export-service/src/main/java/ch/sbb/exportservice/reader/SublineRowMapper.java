package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
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
    final byte prioIdx = getPrioIdx(rs.getString("prio"));

    return Subline.builder()
        .slnid(rs.getString("slnid"))
        .mainlineSlnid(rs.getString("mainlineSlnid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .status(Status.valueOf(rs.getString("status")))
        .sublineType(SublineType.valueOf(rs.getString("subline_type")))
        .concessionType(SublineConcessionType.valueOf(rs.getString("concession_type")))
        .swissSublineNumber(rs.getString("swiss_subline_number"))
        .number(rs.getString("line_number").split("\\|")[prioIdx])
        .shortNumber(rs.getString("short_number").split("\\|")[prioIdx])
        .offerCategory(OfferCategory.valueOf(rs.getString("offer_category").split("\\|")[prioIdx]))
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

  private byte getPrioIdx(String prio) {
    final String[] split = prio.split("\\|");
    byte idx = 0;
    for (byte i = 0; i < split.length; i++) {
      if (split[i].equals("1") || split[i].equals("2")) {
        return i;
      }
      idx = i;
    }
    return idx;
  }

}
