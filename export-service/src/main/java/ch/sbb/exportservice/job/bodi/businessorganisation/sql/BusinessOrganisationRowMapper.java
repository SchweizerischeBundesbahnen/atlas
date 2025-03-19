package ch.sbb.exportservice.job.bodi.businessorganisation.sql;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.bodi.businessorganisation.entity.BusinessOrganisation;
import ch.sbb.exportservice.util.RowMapperUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BusinessOrganisationRowMapper implements RowMapper<BusinessOrganisation> {

  @Override
  public BusinessOrganisation mapRow(ResultSet rs, int rowNum) throws SQLException {
    return BusinessOrganisation.builder()
        .id(rs.getLong("id"))
        .sboid(rs.getString("sboid"))
        .status(Status.valueOf(rs.getString("status")))
        .abbreviationDe(rs.getString("abbreviation_de"))
        .abbreviationFr(rs.getString("abbreviation_fr"))
        .abbreviationIt(rs.getString("abbreviation_it"))
        .abbreviationEn(rs.getString("abbreviation_en"))
        .descriptionDe(rs.getString("description_de"))
        .descriptionFr(rs.getString("description_fr"))
        .descriptionIt(rs.getString("description_it"))
        .descriptionEn(rs.getString("description_en"))
        .organisationNumber(rs.getInt("organisation_number"))
        .contactEnterpriseEmail(rs.getString("contact_enterprise_email"))
        .businessTypes(RowMapperUtil.stringToSet(rs.getString("list_of_business_types"), BusinessType::valueOf))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .number(rs.getString("number"))
        .abbreviation(rs.getString("abbreviation"))
        .businessRegisterName(rs.getString("business_register_name"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .build();
  }

}
