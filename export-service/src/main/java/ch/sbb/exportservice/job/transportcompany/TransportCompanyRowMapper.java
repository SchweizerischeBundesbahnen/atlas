package ch.sbb.exportservice.job.transportcompany;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.exportservice.job.BaseRowMapper;
import ch.sbb.exportservice.job.transportcompany.TransportCompany.TransportCompanyBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class TransportCompanyRowMapper extends BaseRowMapper implements RowMapper<TransportCompany> {

  @Override
  public TransportCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
    TransportCompanyBuilder<?, ?> builder = TransportCompany.builder();
    builder.id(rs.getLong("id"));
    builder.number(rs.getString("number"));
    builder.abbreviation(rs.getString("abbreviation"));
    builder.description(rs.getString("description"));
    builder.businessRegisterName(rs.getString("business_register_name"));
    builder.transportCompanyStatus(TransportCompanyStatus.valueOf(rs.getString("transport_company_status")));
    builder.businessRegisterNumber(rs.getString("business_register_number"));
    builder.enterpriseId(rs.getString("enterprise_id"));
    builder.ricsCode(rs.getString("rics_code"));
    builder.businessOrganisationNumbers(rs.getString("business_organisation_numbers"));
    builder.comment(rs.getString("comment"));
    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    return builder.build();
  }

}
